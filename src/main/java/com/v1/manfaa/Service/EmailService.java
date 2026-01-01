package com.v1.manfaa.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String email;


    public void sendEmail(String toEmail, String subject, String body){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(email);
        message.setSubject(subject);
        message.setTo(toEmail);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendTwoEmail(String toEmail,String toEmail2, String subject, String body){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(email);
        message.setSubject(subject);
        message.setTo(new String[] {toEmail2, toEmail});
        message.setTo();
        message.setText(body);
        mailSender.send(message);
    }
}