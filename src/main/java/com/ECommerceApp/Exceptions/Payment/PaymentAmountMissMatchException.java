package com.ECommerceApp.Exceptions.Payment;

public class PaymentAmountMissMatchException extends RuntimeException {
    public PaymentAmountMissMatchException(String message) {
        super(message);
    }
}
