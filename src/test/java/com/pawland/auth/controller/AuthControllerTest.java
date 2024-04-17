package com.pawland.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawland.auth.dto.request.SignupRequest;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthController authController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
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
            .password("asd123")
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
}
