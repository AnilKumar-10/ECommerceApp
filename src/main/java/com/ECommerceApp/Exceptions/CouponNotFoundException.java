package com.ECommerceApp.Exceptions;

public class CouponNotFoundException extends RuntimeException{
    public CouponNotFoundException(String message) {
        super(message);
    }
}
