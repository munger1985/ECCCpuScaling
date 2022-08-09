package com.example.schedulingtasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Value("${mailto}")
    private String mailto;
    /**
     * 发送纯文本邮件.
     *
     * @param subject 邮件主题
     * @param text    纯文本内容
     */
    public void sendMail( String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(mailto);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
}