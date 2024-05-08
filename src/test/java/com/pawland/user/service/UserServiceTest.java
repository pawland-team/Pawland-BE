package com.pawland.user.service;

import com.pawland.user.domain.User;
import com.pawland.user.dto.request.UserInfoUpdateRequest;
import com.pawland.user.dto.response.UserInfoResponse;
import com.pawland.user.dto.response.UserInfoUpdateResponse;
import com.pawland.user.exception.UserException;
import com.pawland.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionSystemException;

import static com.pawland.user.exception.UserExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @DisplayName("닉네임 중복 확인 시")
    @Nested
    class checkNicknameDuplicate {
        @DisplayName("중복된 닉네임이 없으면 성공한다.")
        @Test
        void checkNicknameDuplicate1() {
            // given
            String nickname = "나는짱";

            // expected
            assertThatCode(() -> userService.checkNicknameDuplicate(nickname))
                .doesNotThrowAnyException();
        }

        @DisplayName("중복된 닉네임이 있으면 실패한다.")
        @Test
        void checkNicknameDuplicate2() {
            // given
            User user = User.builder()
                .email("mid@nav.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();
            userRepository.save(user);

            String duplicateNickname = "나는짱";

            // expected
            assertThatThrownBy(() -> userService.checkNicknameDuplicate(duplicateNickname))
                .isInstanceOf(UserException.AlreadyExistsNickname.class)
                .hasMessageContaining(ALREADY_EXISTS_NICKNAME.getMessage());
        }
    }

    @DisplayName("이메일 중복 확인 시")
    @Nested
    class checkEmailDuplicate {
        @DisplayName("중복된 이메일이 없으면 성공한다.")
        @Test
        void checkEmailDuplicate1() {
            // given
            String email = "midcon@nav.com";

            // expected
            assertThatCode(() -> userService.checkEmailDuplicate(email))
                .doesNotThrowAnyException();
        }

        @DisplayName("중복된 이메일이 있으면 실패한다.")
        @Test
        void checkEmailDuplicate2() {
            // given
            User user = User.builder()
                .email("midcon@nav.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();
            userRepository.save(user);

            String duplicateEmail = "midcon@nav.com";

            // expected
            assertThatThrownBy(() -> userService.checkEmailDuplicate(duplicateEmail))
                .isInstanceOf(UserException.AlreadyExistsEmail.class)
                .hasMessageContaining(ALREADY_EXISTS_EMAIL.getMessage());
        }
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
                .nickname("나는짱123")
                .build();

            // expected
            assertThatThrownBy(() -> userService.register(duplicateEmailUser))
                .isInstanceOf(UserException.AlreadyExistsEmail.class)
                .hasMessageContaining(ALREADY_EXISTS_EMAIL.getMessage());
        }

        @DisplayName("정상적인 정보 입력 시 중복 닉네임이 존재하면 실패한다.")
        @Test
        void register3() {
            // given
            User user = User.builder()
                .email("mid@naver.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();
            userService.register(user);

            User duplicateNicknameUser = User.builder()
                .email("mid1@naver.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();

            // expected
            assertThatThrownBy(() -> userService.register(duplicateNicknameUser))
                .isInstanceOf(UserException.AlreadyExistsNickname.class)
                .hasMessageContaining(ALREADY_EXISTS_NICKNAME.getMessage());
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
            assertThat(result.getProfileImage()).isEqualTo(user.getProfileImage());
        }

        @DisplayName("DB에 저장되지 않은 유저 조회 시도 시 실패한다.")
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

    @DisplayName("유저 정보 수정 시")
    @Nested
    class updateUser {
        @DisplayName("요청한 정보만 수정된다.")
        @Test
        void updateUser1() {
            // given
            User user = User.builder()
                .email("mid@nav.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();
            userRepository.save(user);

            String toUpdateUser = "mid@nav.com";
            UserInfoUpdateRequest request = UserInfoUpdateRequest.builder()
                .nickname("나는변경된짱")
                .build();

            // when
            UserInfoUpdateResponse result = userService.updateUser(toUpdateUser, request);

            // then
            assertThat(result.getId()).isEqualTo(user.getId());
            assertThat(result.getNickname()).isEqualTo("나는변경된짱");
            assertThat(result.getUserDesc()).isEqualTo(user.getIntroduce());
            assertThat(result.getProfileImage()).isEqualTo(user.getProfileImage());
        }

        @DisplayName("닉네임을 누락하거나 빈 값을 입력하면 유저 정보 수정에 실패한다.")
        @Test
        void updateUser2() {
            // given
            User user = User.builder()
                .email("mid@nav.com")
                .password("asd123123")
                .nickname("나는짱")
                .build();
            userRepository.save(user);

            String toUpdateUser = "mid@nav.com";
            UserInfoUpdateRequest request = UserInfoUpdateRequest.builder()
                .nickname("")
                .build();

            // expected
            assertThatThrownBy(() -> userService.updateUser(toUpdateUser, request))
                .isInstanceOf(TransactionSystemException.class)
                .hasMessageContaining("Could not commit JPA transaction");
        }
    }
}
