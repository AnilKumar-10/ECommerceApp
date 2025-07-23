package com.ECommerceApp.Exceptions.ReturnAndRefund;

public class RefundNotFoundException extends RuntimeException{
    public RefundNotFoundException(String message) {
        super(message);
    }
}
