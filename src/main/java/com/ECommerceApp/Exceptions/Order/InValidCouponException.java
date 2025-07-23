package com.ECommerceApp.Exceptions.Order;

public class InValidCouponException extends RuntimeException{
    public InValidCouponException(String message) {
        super(message);
    }
}
