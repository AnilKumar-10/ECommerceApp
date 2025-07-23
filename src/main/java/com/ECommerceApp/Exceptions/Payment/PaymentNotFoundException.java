package com.ECommerceApp.Exceptions.Payment;

public class PaymentNotFoundException extends RuntimeException{
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
