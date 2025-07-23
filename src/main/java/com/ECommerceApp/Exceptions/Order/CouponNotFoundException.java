package com.ECommerceApp.Exceptions.Order;

public class CouponNotFoundException extends RuntimeException{
    public CouponNotFoundException(String message) {
        super(message);
    }
}
