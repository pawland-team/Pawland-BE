package com.pawland.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawland.auth.dto.request.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @DisplayName("올바른 정보를 입력하면 회원가입이 성공한다.")
    @Test
    void signupSuccess() throws Exception {
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
}