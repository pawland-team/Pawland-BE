package com.pawland.mail.service;

import com.pawland.global.config.MailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.UnsupportedEncodingException;

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

    @DisplayName("이메일 전송 성공 Mock 테스트")
    @Test
    void sendEmail1() throws MessagingException, UnsupportedEncodingException {
        // given
        String toEmail = "test@example.com";
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // when
        mailVerificationService.sendEmail(toEmail);

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
        assertThatThrownBy(() -> mailVerificationService.sendEmail(toEmail))
            .isInstanceOf(MailSendException.class)
            .hasMessage("메일 전송에 실패했습니다.");
    }
}
