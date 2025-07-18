package com.ECommerceApp.Exceptions;

public class MailSendException extends RuntimeException{
    public MailSendException(String message) {
        super(message);
    }
}
