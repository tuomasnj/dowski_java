package com.alivold.service;

import javax.mail.MessagingException;

public interface EmailService {
    void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException;

    boolean sendRemindEmail1(String to, String subject, String title, String eventContent);
}
