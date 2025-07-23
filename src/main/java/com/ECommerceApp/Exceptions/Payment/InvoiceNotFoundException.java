package com.ECommerceApp.Exceptions.Payment;

public class InvoiceNotFoundException extends RuntimeException{
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
