package com.ECommerceApp.Exceptions;

public class RefundNotFoundException extends RuntimeException{
    public RefundNotFoundException(String message) {
        super(message);
    }
}
