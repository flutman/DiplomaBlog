package com.example.diploma.service;

public interface MailSender {
    void send(String emailTo, String subject, String message);
}
