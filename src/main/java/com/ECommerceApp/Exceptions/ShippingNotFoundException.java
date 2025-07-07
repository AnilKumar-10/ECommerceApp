package com.ECommerceApp.Exceptions;

public class ShippingNotFoundException extends RuntimeException{
    public ShippingNotFoundException(String message) {
        super(message);
    }
}
