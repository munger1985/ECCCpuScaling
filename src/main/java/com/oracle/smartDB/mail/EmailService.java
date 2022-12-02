package com.oracle.smartDB.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class EmailService {
    public void sendMailWithAttachment(String to, String subject, String body, String fileToAttach) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();


        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(body);
//            FileInputStream inputStream = new FileInputStream(fileToAttach);
            FileSystemResource file = new FileSystemResource(fileToAttach);
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//            helper.addAttachment("logo.jpg", new ByteArrayResource(IOUtils.(inputStream)));

            helper.addAttachment(file.getFilename(), file);
        };

        try {
            javaMailSender.send(preparator);
//            javaMailSender.createMimeMessage()

        } catch (MailException ex) {
            // simply log it and go on...
            ex.printStackTrace();
        }
    }

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
    public void sendMail(String subject, String text) throws MessagingException {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo("jade.shi@oracle.com");
        message.setSubject(subject);
        message.setText(text);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(message.getFrom());
        helper.setTo(message.getTo());
        helper.setSubject(message.getSubject());
        helper.setText(" have attachment 有附件");
        String fileToAttach = "C:\\swdtools\\install.sh";
        FileSystemResource file = new FileSystemResource(fileToAttach);

        helper.addAttachment(file.getFilename(), file);
        javaMailSender.send(mimeMessage);


//        sendMailWithAttachment(mailto, "subject", "brief", "C:\\swdtools\\tt.xlsx");
    }

}