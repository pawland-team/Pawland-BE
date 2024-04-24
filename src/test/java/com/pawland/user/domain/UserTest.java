package com.pawland.user.domain;

import com.pawland.user.dto.request.UserInfoUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.pawland.user.domain.LoginType.*;
import static org.assertj.core.api.Assertions.assertThat;


class UserTest {

    @DisplayName("빌더 사용 시 로그인 타입, 프로필 이미지, 소개글을 입력하지 않으면 엔티티 기본 값으로 저장한다.")
    @Test
    void userBuilder() {
        // given
        User result = User.builder()
            .email("midcondria@naver.com")
            .nickname("나는짱")
            .password("asd213123")
            .build();

        // expected
        assertThat(result.getType()).isEqualTo(NORMAL);
        assertThat(result.getProfileImage()).isEqualTo("");
        assertThat(result.getIntroduce()).isEqualTo("");
    }

    @DisplayName("유저 정보 수정 시 입력한 필드만 수정된다.")
    @Test
    void update1() {
        // given
        User user = User.builder()
            .email("midcondria@naver.com")
            .nickname("나는짱")
            .password("asd213123")
            .build();

        UserInfoUpdateRequest request = UserInfoUpdateRequest.builder()
            .nickname("나아아는짱")
            .profileImage("1234567")
            .introduce("")
            .build();

        // when
        user.update(request.toUser());

        // then
        assertThat(user.getNickname()).isEqualTo("나아아는짱");
        assertThat(user.getProfileImage()).isEqualTo("1234567");
        assertThat(user.getIntroduce()).isEqualTo("");
    }

    @DisplayName("직접 User 객체 입력 시 닉네임, 프로필 이미지, 소개글 외에는 수정되지 않는다.")
    @Test
    void update2() {
        // given
        User user = User.builder()
            .email("midcondria@naver.com")
            .nickname("나는짱")
            .password("asd213123")
            .build();

        User invalidUsage = User.builder()
            .password("111")
            .type(NAVER)
            .email("asd@nav.com")
            .build();

        // when
        user.update(invalidUsage);

        // then
        assertThat(user.getPassword()).isEqualTo("asd213123");
        assertThat(user.getType()).isEqualTo(NORMAL);
        assertThat(user.getEmail()).isEqualTo("midcondria@naver.com");
    }
}