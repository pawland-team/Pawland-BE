package com.pawland.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawland.auth.dto.request.SendVerificationCodeRequest;
import com.pawland.auth.dto.request.EmailDupCheckRequest;
import com.pawland.auth.dto.request.SignupRequest;
import com.pawland.auth.dto.request.VerifyCodeRequest;
import com.pawland.global.config.security.domain.LoginRequest;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.serverCommands().flushDb();
            return null;
        });
    }

    @DisplayName("이메일로 중복 확인 요청 시")
    @Nested
    class emailDupCheck {
        @DisplayName("가입되지 않은 이메일로 중복 확인 시 성공 메시지를 반환한다.")
        @Test
        void emailDupCheck1() throws Exception {
            // given
            EmailDupCheckRequest request = new EmailDupCheckRequest("midcon@nav.com");

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/auth/email-dupcheck")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("사용할 수 있는 이메일입니다."));
        }

        @DisplayName("이미 가입된 이메일로 중복 확인 시 오류 메시지를 반환한다.")
        @Test
        void emailDupCheck2() throws Exception {
            // given
            User user = User.builder()
                .email("midcon@nav.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();
            userRepository.save(user);

            EmailDupCheckRequest request = new EmailDupCheckRequest("midcon@nav.com");

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/auth/email-dupcheck")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("이미 존재하는 유저입니다."));
        }
    }

    @DisplayName("인증 메일 요청 시")
    @Nested
    class sendVerificationCode {
        @DisplayName("인증 메일 요청에 성공한다.")
        @Test
        void sendVerificationCode1() throws Exception {
            // given
            SendVerificationCodeRequest request = new SendVerificationCodeRequest("hyukkind@naver.com");
            MimeMessage mimeMessage = new MimeMessage((Session) null);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/auth/send-verification-code")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value("인증 메일이 발송 되었습니다."));
        }

        @DisplayName("인증 메일 요청에 실패 시 에러 메시지를 출력한다.")
        @Test
        void sendVerificationCode2() throws Exception {
            // given
            SendVerificationCodeRequest request = new SendVerificationCodeRequest("hyukkind@naver.com");
            MimeMessage mimeMessage = new MimeMessage((Session) null);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            doThrow(new MailAuthenticationException("Authentication failed"))
                .when(mailSender)
                .send(mimeMessage);

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/auth/send-verification-code")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").value("메일 전송에 실패했습니다."));
        }
    }

    @DisplayName("인증번호로 인증 요청 시")
    @Nested
    class verifyCode {
        @DisplayName("올바른 인증번호로 인증 요청 시 성공 메시지를 반환한다.")
        @Test
        void verifyCode1() throws Exception {
            // given
            String email = "test@example.com";
            String verificationCode = "123456";
            ValueOperations<String, String> values = redisTemplate.opsForValue();
            values.set(email, verificationCode, Duration.ofMinutes(3));

            VerifyCodeRequest request = VerifyCodeRequest.builder()
                .email(email)
                .code(verificationCode)
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/auth/verify-code")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("이메일 인증이 완료되었습니다."));
        }

        @DisplayName("틀린 인증번호로 인증 요청 시 실패 메시지를 반환한다.")
        @Test
        void verifyCode2() throws Exception {
            // given
            String email = "test@example.com";
            String verificationCode = "123456";
            String WrongCode = "111111";
            ValueOperations<String, String> values = redisTemplate.opsForValue();
            values.set(email, verificationCode, Duration.ofMinutes(3));

            VerifyCodeRequest request = VerifyCodeRequest.builder()
                .email(email)
                .code(WrongCode)
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/auth/verify-code")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("인증번호를 확인해주세요."));
        }

        @DisplayName("메일 인증을 요청하지 않은 이메일로 인증번호 입력 시 실패 메시지를 반환한다.")
        @Test
        void verifyCode3() throws Exception {
            // given
            String email = "test@example.com";
            String notRequestedEmail = "midcon@nav.com";
            String verificationCode = "123456";
            ValueOperations<String, String> values = redisTemplate.opsForValue();
            values.set(email, verificationCode, Duration.ofMinutes(3));

            VerifyCodeRequest request = VerifyCodeRequest.builder()
                .email(notRequestedEmail)
                .code(verificationCode)
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/auth/verify-code")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("인증번호를 확인해주세요."));
        }
    }

    @DisplayName("회원가입 시")
    @Nested
    class signup {
        @DisplayName("필수 정보를 누락하면 회원가입에 실패한다.")
        @Test
        void signup1() throws Exception {
            // given
            SignupRequest request = SignupRequest.builder()
                .email("midcon@nav.com")
                .password("1234")
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/auth/signup")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", Matchers.hasItems("닉네임을 입력해주세요.")))
                .andExpect(cookie().doesNotExist("jwt"));
        }

        @DisplayName("필수 정보를 여러개 누락하면 회원가입에 실패하고, 누락된 필드들의 메시지를 출력한다.")
        @Test
        void signup2() throws Exception {
            // given
            SignupRequest request = SignupRequest.builder()
                .email("midcon@nav.com")
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/auth/signup")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", Matchers.hasItems("비밀번호를 입력해주세요.", "닉네임을 입력해주세요.")))
                .andExpect(cookie().doesNotExist("jwt"));
        }

        @DisplayName("이메일 인증을 했을 때")
        @Nested
        class emailVerified {
            @DisplayName("올바른 정보를 입력하면 회원가입에 성공한다.")
            @Test
            void signup1() throws Exception {
                // given
                SignupRequest request = SignupRequest.builder()
                    .email("midcon@nav.com")
                    .password("1234")
                    .nickname("나는짱")
                    .build();
                ValueOperations<String, String> values = redisTemplate.opsForValue();
                values.set("midcon@nav.com", "ok");

                String json = objectMapper.writeValueAsString(request);

                // expected
                mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                    )
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$").value("회원가입 되었습니다."))
                    .andExpect(cookie().exists("jwt"));
            }

            @DisplayName("이미 가입된 이메일로 가입 시 회원가입에 실패한다.")
            @Test
            void signup2() throws Exception {
                // given
                User user = User.builder()
                    .email("midcon@nav.com")
                    .password("asd123123")
                    .nickname("나는짱")
                    .build();
                userRepository.save(user);
                ValueOperations<String, String> values = redisTemplate.opsForValue();
                values.set("midcon@nav.com", "ok");

                SignupRequest request = SignupRequest.builder()
                    .email("midcon@nav.com")
                    .password("asd123123")
                    .nickname("나는짱")
                    .build();

                String json = objectMapper.writeValueAsString(request);

                // expected
                mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").value("이미 존재하는 유저입니다."))
                    .andExpect(cookie().doesNotExist("jwt"));
            }
        }

        @DisplayName("이메일 인증을 안했을 때")
        @Nested
        class emailNotVerified {
            @DisplayName("올바른 정보를 입력하면 회원가입에 실패한다.")
            @Test
            void signup1() throws Exception {
                // given
                SignupRequest request = SignupRequest.builder()
                    .email("midcon@nav.com")
                    .password("1234")
                    .nickname("나는짱")
                    .build();

                String json = objectMapper.writeValueAsString(request);

                // expected
                mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                    )
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$").value("이메일 인증이 인증되지 않은 유저입니다."))
                    .andExpect(cookie().doesNotExist("jwt"));
            }

            @DisplayName("이미 가입된 이메일로 가입 시 회원가입에 실패한다.")
            @Test
            void signup2() throws Exception {
                // given
                User user = User.builder()
                    .email("midcon@nav.com")
                    .password("asd123123")
                    .nickname("나는짱")
                    .build();
                userRepository.save(user);

                SignupRequest request = SignupRequest.builder()
                    .email("midcon@nav.com")
                    .password("asd123123")
                    .nickname("나는짱")
                    .build();

                String json = objectMapper.writeValueAsString(request);

                // expected
                mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                    )
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$").value("이메일 인증이 인증되지 않은 유저입니다."))
                    .andExpect(cookie().doesNotExist("jwt"));
            }
        }
    }

    @DisplayName("로그인 시")
    @Nested
    class login {
        @DisplayName("등록된 유저와 이메일, 비밀번호가 일치하면 로그인에 성공한다.")
        @Test
        void login1() throws Exception {
            // given
            User user = User.builder()
                .email("midcon@nav.com")
                .password(passwordEncoder.encode("asd123123"))
                .nickname("나는짱")
                .build();
            userRepository.save(user);

            LoginRequest request = LoginRequest.builder()
                .email("midcon@nav.com")
                .password("asd123123")
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("로그인에 성공했습니다."))
                .andExpect(cookie().exists("jwt"));
        }

        @DisplayName("틀린 비밀번호로 로그인 요청 시 로그인에 실패한다.")
        @Test
        void login2() throws Exception {
            // given
            User user = User.builder()
                .email("midcon@nav.com")
                .password(passwordEncoder.encode("asd123"))
                .nickname("나는짱")
                .build();
            userRepository.save(user);

            LoginRequest request = LoginRequest.builder()
                .email("midcon@nav.com")
                .password("asd123123")
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("아이디 혹은 비밀번호가 올바르지 않습니다."))
                .andExpect(cookie().doesNotExist("jwt"));
        }

        @DisplayName("등록되지 않는 이메일로 로그인 요청 시 로그인에 실패한다.")
        @Test
        void login3() throws Exception {
            // given
            User user = User.builder()
                .email("midcon@nav.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();
            userRepository.save(user);

            LoginRequest request = LoginRequest.builder()
                .email("mid@nav.com")
                .password("asd123123")
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("아이디 혹은 비밀번호가 올바르지 않습니다."))
                .andExpect(cookie().doesNotExist("jwt"));
        }
    }
}
