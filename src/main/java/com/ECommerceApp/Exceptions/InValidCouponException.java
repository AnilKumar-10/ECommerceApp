package com.ECommerceApp.Exceptions;

public class InValidCouponException extends RuntimeException{
    public InValidCouponException(String message) {
        super(message);
    }
}
