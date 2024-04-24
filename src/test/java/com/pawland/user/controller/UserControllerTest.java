package com.pawland.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawland.global.config.TestSecurityConfig;
import com.pawland.global.utils.PawLandMockUser;
import com.pawland.user.dto.request.UserInfoUpdateRequest;
import com.pawland.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @DisplayName("내 프로필 조회에 성공한다.")
    @PawLandMockUser
    @Test
    void getUserInfo1() throws Exception {
        // expected
        mockMvc.perform(get("/api/users/my-info")
                .contentType(APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.nickname").value("나는짱"))
            .andExpect(jsonPath("$.email").value("midcondria@naver.com"))
            .andExpect(jsonPath("$.userDesc").value(""))
            .andExpect(jsonPath("$.stars").isNumber());
    }

    @DisplayName("내 정보 수정 시")
    @Nested
    class updateUserInfo {
        @DisplayName("변경한 값만 수정된다.")
        @PawLandMockUser
        @Test
        void updateUserInfo1() throws Exception {
            //given
            UserInfoUpdateRequest request = UserInfoUpdateRequest.builder()
                .nickname("변경할 값")
                .profileImage("변경할 이미지")
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(patch("/api/users/my-info")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nickname").value("변경할 값"))
                .andExpect(jsonPath("$.profileImage").value("변경할 이미지"))
                .andExpect(jsonPath("$.userDesc").value(""));
        }

        @DisplayName("닉네임 누락이 누락되거나 빈 값이면 에러 메시지를 출력한다.")
        @Test
        void updateUserInfo2() throws Exception {
            UserInfoUpdateRequest request = UserInfoUpdateRequest.builder()
                .nickname("")
                .profileImage("변경할 이미지")
                .introduce("자기 소개")
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(patch("/api/users/my-info")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("닉네임을 입력해주세요."));
        }
    }
}