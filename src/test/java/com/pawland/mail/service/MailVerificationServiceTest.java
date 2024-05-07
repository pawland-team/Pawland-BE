package com.pawland.mail.service;

import com.pawland.global.config.MailConfig;
import com.pawland.global.exception.InvalidCodeException;
import com.pawland.global.exception.InvalidUserException;
import com.pawland.mail.repository.MailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.UnsupportedEncodingException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
class MailVerificationServiceTest {

    @Autowired
    private MailConfig mailConfig;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private MailVerificationService mailVerificationService;

    @Autowired
    private MailRepository mailRepository;

    @Mock
    private MailRepository mockMailRepository;

    @InjectMocks
    private MailVerificationService mockMailService;

    @DisplayName("이메일 전송 성공 Mock 테스트")
    @Test
    void sendEmail1() throws MessagingException, UnsupportedEncodingException {
        // given
        String toEmail = "test@example.com";
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // when
        mailVerificationService.sendVerificationCode(toEmail);

        // then
        verify(mailSender).send(mimeMessage);
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @DisplayName("이메일 전송 실패 Mock 테스트")
    @Test
    void sendEmail2() {
        // given
        String toEmail = "test@example.com";
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailAuthenticationException("Authentication failed"))
            .when(mailSender)
            .send(mimeMessage);

        // expected
        assertThatThrownBy(() -> mailVerificationService.sendVerificationCode(toEmail))
            .isInstanceOf(MailSendException.class)
            .hasMessage("메일 전송에 실패했습니다.");
    }

    @DisplayName("이메일 전송 성공 시 인증 번호가 redis에 저장된다.")
    @Test
    void sendEmail3() throws MessagingException, UnsupportedEncodingException {
        // given
        String toEmail = "test@example.com";
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // when
        mailVerificationService.sendVerificationCode(toEmail);
        String result = mailRepository.findByEmail(toEmail);

        // then
        assertThat(result).isNotNull();
        assertThat(result.length()).isEqualTo(6);
    }

    @DisplayName("올바른 인증번호를 입력하면 redis에 성공 여부를 저장한다.")
    @Test
    void verifyCode1() {
        // given
        String email = "test@example.com";
        String verificationCode = "123456";
        mailRepository.save(email, verificationCode, Duration.ofMinutes(3));

        // when
        mailVerificationService.verifyCode(email, verificationCode);
        String result = mailRepository.findByEmail(email);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("ok");
    }

    @DisplayName("틀린 인증번호를 입력하면 예외를 던진다.")
    @Test
    void verifyCode2() {
        // given
        String email = "test@example.com";
        String verificationCode = "123456";
        String WrongCode = "111111";
        mailRepository.save(email, verificationCode, Duration.ofMinutes(3));

        // expected
        assertThatThrownBy(() -> mailVerificationService.verifyCode(email, WrongCode))
            .isInstanceOf(InvalidCodeException.class)
            .hasMessage("인증번호를 확인해주세요.");
    }

    @DisplayName("메일 인증을 요청하지 않은 이메일로 인증 번호를 입력하면 예외를 던진다.")
    @Test
    void verifyCode3() {
        // given
        String email = "test@example.com";
        String notRequestedEmail = "midcon@nav.com";
        String verificationCode = "123456";
        mailRepository.save(email, verificationCode, Duration.ofMinutes(3));

        // expected
        assertThatThrownBy(() -> mailVerificationService.verifyCode(notRequestedEmail, verificationCode))
            .isInstanceOf(InvalidCodeException.class)
            .hasMessage("인증번호를 확인해주세요.");
    }

    @DisplayName("이메일 인증된 유저가 회원가입 요청 시 성공한다.")
    @Test
    void checkEmailVerification1() {
        // given
        String verifiedEmail = "test@example.com";
        String authSuccessStatus = "ok";
        when(mockMailRepository.findByEmail(verifiedEmail)).thenReturn(authSuccessStatus);

        // when
        mockMailService.checkEmailVerification(verifiedEmail);

        // then
        verify(mockMailRepository).findByEmail(verifiedEmail);
        verify(mockMailRepository, times(1)).findByEmail(verifiedEmail);
    }

    @DisplayName("이메일을 인증하지 않은 유저가 회원가입 요청 시 실패한다.")
    @Test
    void checkEmailVerification2() {
        // given
        String authWaitingEmail = "test@example.com";
        String authWaitingStatus = "123456";
        when(mockMailRepository.findByEmail(authWaitingEmail)).thenReturn(authWaitingStatus);

        // expected
        assertThatThrownBy(() ->  mockMailService.checkEmailVerification(authWaitingEmail))
            .isInstanceOf(InvalidUserException.class)
            .hasMessage("이메일 인증이 인증되지 않은 유저입니다.");
    }

    @DisplayName("인증 요청을 하지 않았거나 인증한지 오래된 유저가 요청 시 실패한다.")
    @Test
    void checkEmailVerification3() {
        // given
        String authNotProcessingEmail = "test@example.com";
        String authNotProcessingStatus = null;
        when(mockMailRepository.findByEmail(authNotProcessingEmail)).thenReturn(authNotProcessingStatus);

        // expected
        assertThatThrownBy(() ->  mockMailService.checkEmailVerification(authNotProcessingEmail))
            .isInstanceOf(InvalidUserException.class)
            .hasMessage("이메일 인증이 인증되지 않은 유저입니다.");
    }
}
