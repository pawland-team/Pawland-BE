package com.pawland.mail.service;

import com.pawland.global.config.MailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailVerificationService implements MailService {

    private final MailConfig mailConfig;
    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String toEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = createEmailVerificationMessage(toEmail);
        try {
            mailSender.send(message);
        } catch (RuntimeException e) {
            log.error("[메일 전송 실패]");
            throw new MailSendException("메일 전송에 실패했습니다.");
        }
    }

    private MimeMessage createEmailVerificationMessage(String toEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(mailConfig.getFromEmail(), "나는짱");
        helper.setTo(toEmail);
        helper.setSubject("PAWLAND 이메일 인증");
        String template = createTemplate();
        helper.setText(template, true);
        return message;
    }

    private String createTemplate() {
        String template = "<html><body>";
        template += "<p>안녕하세요, PAWLAND입니다.</p>";
        template += "<p>인증 번호는 아래와 같습니다:</p>";
        template += "<h2>" + generateVerificationCode() + "</h2>";
        template += "<p>이 인증 번호를 입력하여 인증을 완료해주세요.</p>";
        template += "</body></html>";
        return template;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(999999) + 1;
        return String.format("%06d", randomNumber);
    }
}
