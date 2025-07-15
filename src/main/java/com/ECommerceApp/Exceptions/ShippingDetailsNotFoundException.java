package com.ECommerceApp.Exceptions;

public class ShippingDetailsNotFoundException extends RuntimeException{
    public ShippingDetailsNotFoundException(String message) {
        super(message);
    }
}
