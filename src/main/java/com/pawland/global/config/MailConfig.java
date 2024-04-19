package com.pawland.global.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "gmail")
public class MailConfig {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final SmtpProperties smtpProperties;

    public String getFromEmail(){
        return username;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setJavaMailProperties(getProperties());
        return mailSender;
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", smtpProperties.auth);
        properties.put("mail.smtp.starttls.enable", smtpProperties.starttlsEnable);
        properties.put("mail.smtp.starttls.required", smtpProperties.starttlsRequired);
        properties.put("mail.smtp.connectiontimeout", smtpProperties.connectionTimeout);
        properties.put("mail.smtp.timeout", smtpProperties.timeout);
        properties.put("mail.smtp.writetimeout", smtpProperties.writeTimeout);
        log.info("[props] = {}",properties);
        return properties;
    }

    @Getter
    @ToString
    @RequiredArgsConstructor
    private static class SmtpProperties {
        private final boolean auth;
        private final boolean starttlsEnable;
        private final boolean starttlsRequired;
        private final int connectionTimeout;
        private final int timeout;
        private final int writeTimeout;
    }
}
