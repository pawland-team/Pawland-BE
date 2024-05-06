package com.pawland.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawland.global.config.TestSecurityConfig;
import com.pawland.global.utils.PawLandMockUser;
import com.pawland.image.dto.request.ImageUploadRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("정상적인 요청일 시 Presigned Url을 발급한다.")
    @PawLandMockUser
    @Test
    void uploadPostImage1() throws Exception {
        // given
        String fileName = "test";
        ImageUploadRequest request = new ImageUploadRequest(fileName);

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/image")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.presignedUrl").isString());
    }

    @DisplayName("요청한 파일명이 Null 일 시 에러 메시지를 출력한다.")
    @PawLandMockUser
    @Test
    void uploadPostImage2() throws Exception {
        // given
        String fileName = null;
        ImageUploadRequest request = new ImageUploadRequest(fileName);

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/image")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("파일명을 확인해주세요."));
    }

    @DisplayName("요청한 파일명이 공백일 시 에러 메시지를 출력한다.")
    @PawLandMockUser
    @Test
    void uploadPostImage3() throws Exception {
        // given
        String fileName = "  ";
        ImageUploadRequest request = new ImageUploadRequest(fileName);

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/image")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("파일명을 확인해주세요."));

    }
}
