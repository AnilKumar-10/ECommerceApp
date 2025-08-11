package com.ECommerceApp.Exceptions.Order;

public class InValidActionException extends RuntimeException{
    public InValidActionException(String message) {
        super(message);
    }
}
