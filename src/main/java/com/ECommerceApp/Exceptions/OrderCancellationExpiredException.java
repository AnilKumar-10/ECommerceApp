package com.ECommerceApp.Exceptions;

public class OrderCancellationExpiredException extends RuntimeException{
    public OrderCancellationExpiredException(String message) {
        super(message);
    }
}
