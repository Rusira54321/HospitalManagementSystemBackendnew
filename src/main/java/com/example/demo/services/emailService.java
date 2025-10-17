package com.example.demo.services;

import com.example.demo.Interfaces.INotificationService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class emailService implements INotificationService {

    private final JavaMailSender mailSender;
    public emailService(JavaMailSender mailSender)
    {
        this.mailSender  = mailSender;
    }

    @Override
    public void sendNotification(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("rusira42103@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
