package com.pawland.mail.service;

import com.pawland.global.config.MailConfig;
import com.pawland.global.exception.InvalidCodeException;
import com.pawland.global.exception.InvalidUserException;
import com.pawland.mail.repository.VerifyCodeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MailVerificationService {

    private final MailConfig mailConfig;
    private final JavaMailSender mailSender;
    private final VerifyCodeRepository verifyCodeRepository;

    @Transactional
    public void sendVerificationCode(String toEmail) throws MessagingException, UnsupportedEncodingException {
        String verificationCode = generateVerificationCode();
        verifyCodeRepository.save(toEmail, verificationCode, Duration.ofMinutes(3));

        MimeMessage message = createMessage(toEmail, verificationCode);
        try {
            mailSender.send(message);
        } catch (RuntimeException e) {
            log.error("[메일 전송 실패]");
            throw new MailSendException("메일 전송에 실패했습니다.");
        }
    }

    @Transactional
    public void verifyCode(String email, String code) {
        String savedVerificationCode = verifyCodeRepository.findByEmail(email);
        boolean isMatched = code.equals(savedVerificationCode);
        if (isMatched) {
            verifyCodeRepository.save(email, "ok", Duration.ofMinutes(5));
        } else {
            log.error("[메일 인증 실패]");
            throw new InvalidCodeException();
        }
    }

    public void checkEmailVerification(String email) {
        String savedCode = verifyCodeRepository.findByEmail(email);
        boolean isVerifiedEmail = savedCode != null && savedCode.equals("ok");
        if (!isVerifiedEmail) {
            log.error("[이메일 인증이 안된 유저]");
            throw new InvalidUserException();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(999999) + 1;
        return String.format("%06d", randomNumber);
    }

    private MimeMessage createMessage(String toEmail, String verificationCode) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(mailConfig.getFromEmail(), "나는짱");
        helper.setTo(toEmail);
        helper.setSubject("PAWLAND 이메일 인증");
        String template = createTemplate(verificationCode);
        helper.setText(template, true);
        return message;
    }

    private String createTemplate(String verificationCode) {
        String template = "<html><body>";
        template += "<p>안녕하세요, PAWLAND입니다.</p>";
        template += "<p>인증 번호는 아래와 같습니다:</p>";
        template += "<h2>" + verificationCode + "</h2>";
        template += "<p>이 인증 번호를 입력하여 인증을 완료해주세요.</p>";
        template += "</body></html>";
        return template;
    }
}
