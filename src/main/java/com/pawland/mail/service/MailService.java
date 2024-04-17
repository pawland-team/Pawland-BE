package com.pawland.mail.service;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public interface MailService {

    public void sendEmail(String toEmail, String title, String text) throws MessagingException, UnsupportedEncodingException;
}
