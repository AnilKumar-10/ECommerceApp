package com.ECommerceApp.Exceptions.Order;

public class OrderCancellationExpiredException extends RuntimeException{
    public OrderCancellationExpiredException(String message) {
        super(message);
    }
}
