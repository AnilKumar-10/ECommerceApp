package com.ECommerceApp.Exceptions.User;

public class AddressNotFoundException extends RuntimeException{
    public AddressNotFoundException(String message) {
        super(message);
    }
}
