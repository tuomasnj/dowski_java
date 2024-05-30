package com.alivold.service.impl;

import com.alivold.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sendUser;

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(sendUser);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }

    public boolean sendRemindEmail1(String to, String subject){
        String htmlContent = "<html>"
                + "<head><style>"
                + "h1 {color: blue;}"
                + "p {font-size: 14px; color: green;}"
                + "</style></head>"
                + "<body>"
                + "<h1>事件标题</h1>"
                + "<p>备忘录事件提醒：</p >"
                + "<p style='color: red;'>请别忘了今天的计划哟，您将要在今天12:00和dowski亲亲</p >"
                + "</body></html>";
        try {
            sendHtmlEmail(to, subject, htmlContent);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
