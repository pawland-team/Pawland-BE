package com.pawland.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawland.global.config.TestSecurityConfig;
import com.pawland.global.utils.PawLandMockUser;
import com.pawland.post.dto.request.PostWriteRequest;
import com.pawland.post.repository.PostRepository;
import com.pawland.user.repository.UserRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        postRepository.deleteAllInBatch();
    }

    @DisplayName("게시글 작성 시")
    @Nested
    class uploadPost {
        @DisplayName("필수 값(제목) 입력 시 게시글 작성에 성공한다.")
        @PawLandMockUser
        @Test
        void writePost1() throws Exception {
            // given
            PostWriteRequest request = PostWriteRequest.builder()
                .title("제목")
                .content("내용")
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/post")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("게시글이 등록되었습니다."));
        }

        @DisplayName("필수 값(제목) 이외의 값은 누락해도 게시글이 작성에 성공한다.")
        @PawLandMockUser
        @Test
        void writePost2() throws Exception {
            // given
            PostWriteRequest request = PostWriteRequest.builder()
                .title("제목")
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/post")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("게시글이 등록되었습니다."));
        }

        @DisplayName("필수 값(제목) 누락 시 게시글 작성에 실패한다.")
        @PawLandMockUser
        @Test
        void writePost3() throws Exception {
            // given
            PostWriteRequest request = PostWriteRequest.builder()
                .content("내용")
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/post")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("제목을 입력해주세요."));
        }

        @DisplayName("설정된 지역 이외의 값 입력 시 게시글 작성에 실패한다.")
        @PawLandMockUser
        @Test
        void writePost4() throws Exception {
            // given
            PostWriteRequest request = PostWriteRequest.builder()
                .title("제목")
                .region("나는짱")
                .build();

            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/api/post")
                    .contentType(APPLICATION_JSON)
                    .content(json)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("지역 값을 확인해주세요."));
        }
    }
}
