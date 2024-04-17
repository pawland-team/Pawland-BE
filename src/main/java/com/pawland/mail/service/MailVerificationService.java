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

@Service
@RequiredArgsConstructor
@Slf4j
public class MailVerificationService implements MailService{

    private final MailConfig mailConfig;
    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String toEmail, String title, String text) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = getEmailVa(toEmail);
        try {
            mailSender.send(message);
        } catch (RuntimeException e) {
            log.error("[메일 전송 실패]");
            throw new IllegalArgumentException(e.getMessage()); // TODO: 커스텀 예외 생성
        }
    }

    private MimeMessage getEmailVa(String toEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(mailConfig.getFromEmail(), "나는짱");
        helper.setTo(toEmail);
        helper.setSubject("PAWLAND 이메일 인증");

        String template = "";
        template += "<div style='margin:20px;'>";
        template += "<h1> 안녕하세요, PAWLAND 입니다. </h1>";
        template += "<br>";
        template += "<br>";
        template += "<p>감사합니다.<p>";
        template += "<br>";
        template += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        template += "<h3 style='color:blue;'>회원가입 인증 링크입니다.</h3>";
        template += "LINK : <strong>";
        template += "</div>";
        helper.setText(template);
        return message;
    }
}
