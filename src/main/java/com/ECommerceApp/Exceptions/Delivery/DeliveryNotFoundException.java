package com.ECommerceApp.Exceptions.Delivery;

public class DeliveryNotFoundException extends RuntimeException{
    public DeliveryNotFoundException(String message) {
        super(message);
    }
}
