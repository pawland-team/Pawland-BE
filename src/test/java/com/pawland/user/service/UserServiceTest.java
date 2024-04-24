package com.pawland.user.service;

import com.pawland.global.config.AppConfig;
import com.pawland.global.exception.AlreadyExistsUserException;
import com.pawland.user.domain.User;
import com.pawland.user.dto.response.UserInfoResponse;
import com.pawland.user.exception.UserException;
import com.pawland.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.pawland.user.exception.UserExceptionMessage.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @DisplayName("이메일 중복 확인 시 중복된 이메일이 없으면 성공한다.")
    @Test
    void checkEmailDuplicate1() {
        // given
        String email = "midcon@nav.com";

        // expected
        assertThatCode(() -> userService.checkEmailDuplicate(email))
            .doesNotThrowAnyException();
    }

    @DisplayName("이메일 중복 확인 시 중복된 이메일이 있으면 실패한다.")
    @Test
    void checkEmailDuplicate2() {
        // given
        User user = User.builder()
            .email("mid@nav.com")
            .password("asd123123")
            .nickname("나는짱")
            .build();
        userRepository.save(user);

        // expected
        assertThatThrownBy(() -> userService.checkEmailDuplicate("mid@nav.com"))
            .isInstanceOf(AlreadyExistsUserException.class)
            .hasMessageContaining("이미 존재하는 유저입니다.");
    }

    @DisplayName("유저 등록 시")
    @Nested
    class register {
        @DisplayName("정상적인 정보 입력 시 중복 이메일이 아니라면 유저 정보를 DB에 저장한다.")
        @Test
        void register1() {
            // given
            User user = User.builder()
                .email("mid@nav.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();

            // when
            userService.register(user);
            User result = userRepository.findByEmail(user.getEmail())
                .orElseThrow(IllegalArgumentException::new);

            // then
            assertThat(result.getEmail()).isEqualTo(user.getEmail());
            assertThat(result.getNickname()).isEqualTo(user.getNickname());
        }

        @DisplayName("정상적인 정보 입력 시 중복 이메일이 존재하면 실패한다.")
        @Test
        void register2() {
            // given
            User user = User.builder()
                .email("mid@nav.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();
            userService.register(user);

            User duplicateEmailUser = User.builder()
                .email("mid@nav.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();

            // expected
            assertThatThrownBy(() -> userService.register(duplicateEmailUser))
                .isInstanceOf(AlreadyExistsUserException.class)
                .hasMessageContaining("이미 존재하는 유저입니다.");
        }
    }

    @DisplayName("유저 정보 단건 조회 시")
    @Nested
    class getUserInfo {
        @DisplayName("유저 정보 조회에 성공한다.")
        @Test
        void getUserInfo1() {
            // given
            User user = User.builder()
                .email("mid@nav.com")
                .password("asd123123")
                .nickname("나는짱")
                .introduce("나아는짱")
                .profileImage(appConfig.getDefaultImage())
                .build();
            userRepository.save(user);

            String toGetEmail = "mid@nav.com";
            double tempStars = 3.5;  // TODO 평점 조회 로직 구현시 수정 필요

            // when
            UserInfoResponse result = userService.getUserInfo(toGetEmail);

            // then
            assertThat(result.getId()).isNotNull();
            assertThat(result.getEmail()).isEqualTo(user.getEmail());
            assertThat(result.getNickname()).isEqualTo(user.getNickname());
            assertThat(result.getUserDesc()).isEqualTo(user.getIntroduce());
            assertThat(result.getStars()).isEqualTo(tempStars);
            assertThat(result.getProfileImage()).isNotBlank();
    }

        @DisplayName("DB에 저장되지 않은 유저를 조회 시도 시 실패한다.")
        @Test
        void getUserInfo2() {
            User user = User.builder()
                .email("mid@nav.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();
            userRepository.save(user);

            String toGetEmail = "midcon@nav.com";

            // expected
            assertThatThrownBy(() -> userService.getUserInfo(toGetEmail))
                .isInstanceOf(UserException.NotFoundUser.class)
                .hasMessageContaining(USER_NOT_FOUND.getMessage());
        }
    }
}
