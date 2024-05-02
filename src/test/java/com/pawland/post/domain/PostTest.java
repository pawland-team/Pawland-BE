package com.pawland.post.domain;

import com.pawland.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.pawland.global.domain.DefaultImage.DEFAULT_POST_IMAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostTest {

    @DisplayName("빌더 사용 시 ")
    @Nested
    class postBuilder {
        @DisplayName("썸네일, 지역을 설정하지 않으면 기본 설정 값으로 저장한다.")
        @Test
        void postBuilder1() {
            // given
            User author = User.builder()
                .email("midcon@naver.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();

            Post result = Post.builder()
                .title("글 제목")
                .content("글 내용")
                .author(author)
                .build();

            // expected
            assertThat(result.getThumbnail()).isEqualTo(DEFAULT_POST_IMAGE.value());
            assertThat(result.getRegion()).isEqualTo(Region.SEOUL);
        }

        @DisplayName("썸네일, 지역을 설정하면 입력 값대로 저장한다.")
        @Test
        void postBuilder2() {
            // given
            User author = User.builder()
                .email("midcon@naver.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();

            Post result = Post.builder()
                .title("글 제목")
                .content("글 내용")
                .region(Region.fromString("울산"))
                .thumbnail("이미지.png")
                .author(author)
                .build();

            // expected
            assertThat(result.getThumbnail()).isEqualTo("이미지.png");
            assertThat(result.getRegion()).isEqualTo(Region.ULSAN);
        }

        @DisplayName("설정 지역 이외의 지역 입력 시 예외를 던진다.")
        @Test
        void postBuilder3() {
            // given
            User author = User.builder()
                .email("midcon@naver.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();

            // expected
            assertThatThrownBy(() -> Post.builder()
                    .title("글 제목")
                    .content("글 내용")
                    .region(Region.fromString("나는짱"))
                    .thumbnail("이미지.png")
                    .author(author)
                    .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지역 값을 확인해주세요.");
        }
    }
}
