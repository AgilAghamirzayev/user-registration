package com.vabiss.userregistration.service.email;

public interface EmailSender {
    void send(String to, String body, String subject);
}