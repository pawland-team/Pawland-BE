package com.pawland.user.service;

import com.pawland.global.exception.AlreadyExistsUserException;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
            .phoneNumber("010-1234-5678")
            .build();
        userRepository.save(user);

        // expected
        assertThatThrownBy(() -> userService.checkEmailDuplicate("mid@nav.com"))
            .isInstanceOf(AlreadyExistsUserException.class)
            .hasMessageContaining("이미 존재하는 유저입니다.");
    }

    @DisplayName("정상적인 정보 입력 시 중복 이메일이 아니라면 유저 정보를 DB에 저장한다.")
    @Test
    void register1() {
        // given
        User user = User.builder()
            .email("mid@nav.com")
            .password("asd123123")
            .phoneNumber("010-1234-5678")
            .build();

        // when
        userService.register(user);
        User result = userRepository.findByEmail(user.getEmail())
            .orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
        assertThat(result.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
    }

    @DisplayName("정상적인 정보 입력 시 중복 이메일이 존재하면 실패한다.")
    @Test
    void register2() {
        // given
        User user = User.builder()
            .email("mid@nav.com")
            .password("asd123123")
            .phoneNumber("010-1234-5678")
            .build();

        userService.register(user);
        User duplicateUser = User.builder()
            .email("mid@nav.com")
            .password("asd123123")
            .phoneNumber("010-1234-5678")
            .build();

        // expected
        assertThatThrownBy(() -> userService.register(duplicateUser))
            .isInstanceOf(AlreadyExistsUserException.class)
            .hasMessageContaining("이미 존재하는 유저입니다.");
    }
}
