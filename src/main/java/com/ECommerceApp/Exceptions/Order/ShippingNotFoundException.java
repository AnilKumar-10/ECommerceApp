package com.ECommerceApp.Exceptions.Order;

public class ShippingNotFoundException extends RuntimeException{
    public ShippingNotFoundException(String message) {
        super(message);
    }
}
