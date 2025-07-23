package com.ECommerceApp.Exceptions.Notification;

public class MailSendException extends RuntimeException{
    public MailSendException(String message) {
        super(message);
    }
}
