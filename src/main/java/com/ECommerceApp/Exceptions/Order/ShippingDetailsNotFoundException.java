package com.ECommerceApp.Exceptions.Order;

public class ShippingDetailsNotFoundException extends RuntimeException{
    public ShippingDetailsNotFoundException(String message) {
        super(message);
    }
}
