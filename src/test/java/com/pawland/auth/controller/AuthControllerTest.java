package com.pawland.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawland.auth.dto.request.SendVerificationCodeRequest;
import com.pawland.auth.dto.request.EmailDupCheckRequest;
import com.pawland.auth.dto.request.SignupRequest;
import com.pawland.global.config.security.domain.LoginRequest;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

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

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

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
            .phoneNumber("010-1234-5678")
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

    @DisplayName("올바른 정보를 입력하면 회원가입에 성공한다.")
    @Test
    void signup1() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
            .email("midcon@nav.com")
            .password("1234")
            .phoneNumber("010-1234-5678")
            .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/auth/signup")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @DisplayName("필수 정보를 누락하면 회원가입에 실패한다.")
    @Test
    void signup2() throws Exception {
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
            .andExpect(jsonPath("$", Matchers.hasItems("전화번호를 입력해주세요.")));
    }

    @DisplayName("필수 정보를 여러개 누락하면 회원가입에 실패하고, 누락된 필드들의 메시지를 출력한다.")
    @Test
    void signup3() throws Exception {
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
            .andExpect(jsonPath("$", Matchers.hasItems("비밀번호를 입력해주세요.", "전화번호를 입력해주세요.")));
    }

    @DisplayName("이미 가입된 이메일로 가입 시 회원가입에 실패한다.")
    @Test
    void signup4() throws Exception {
        // given
        User user = User.builder()
            .email("midcon@nav.com")
            .password("asd123123")
            .phoneNumber("010-1234-5678")
            .build();
        userRepository.save(user);

        SignupRequest request = SignupRequest.builder()
            .email("midcon@nav.com")
            .password("asd123123")
            .phoneNumber("010-1234-5678")
            .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/auth/signup")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$").value("이미 존재하는 유저입니다."));
    }

    @DisplayName("등록된 유저와 이메일, 비밀번호가 일치하면 로그인에 성공한다.")
    @Test
    void login1() throws Exception {
        // given
        User user = User.builder()
            .email("midcon@nav.com")
            .password(passwordEncoder.encode("asd123123"))
            .phoneNumber("010-1234-5678")
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
            .phoneNumber("010-1234-5678")
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
            .phoneNumber("010-1234-5678")
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
