package com.ECommerceApp.Exceptions.Notification;

public class NotificationNotFoundException extends RuntimeException{
    public NotificationNotFoundException(String message) {
        super(message);
    }
}
