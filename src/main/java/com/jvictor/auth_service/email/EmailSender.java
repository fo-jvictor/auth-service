package com.jvictor.auth_service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;

    @Value("${application.email}")
    private String MY_APP_EMAIL;

    public void sendEmail(String to, String subject, String content) {
        try {
            System.out.println("Sending confirmation email to: " + to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            message.setFrom(MY_APP_EMAIL);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Exception happened while sending email: " + e.getMessage());
        }
    }
}
