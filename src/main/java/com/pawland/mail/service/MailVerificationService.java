package com.pawland.mail.service;

import com.pawland.global.config.MailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailVerificationService implements MailService{

    private final MailConfig mailConfig;
    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String toEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = createEmailVerificationMessage(toEmail);
        try {
            mailSender.send(message);
        } catch (RuntimeException e) {
            log.error("[메일 전송 실패]");
            throw new IllegalArgumentException(e.getMessage()); // TODO: 커스텀 예외 생성
        }
    }

    private MimeMessage createEmailVerificationMessage(String toEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(mailConfig.getFromEmail(), "나는짱");
        helper.setTo(toEmail);
        helper.setSubject("PAWLAND 이메일 인증");
        String template = createTemplate();
        helper.setText(template);
        return message;
    }

    private static String createTemplate() {
        String template = "";
        template += "<div style='margin:20px;'>";
        template += "<h1> 안녕하세요, PAWLAND 입니다. </h1>";
        template += "</div>";
        return template;
    }
}
