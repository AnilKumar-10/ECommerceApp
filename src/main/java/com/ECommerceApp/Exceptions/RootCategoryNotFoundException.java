package com.ECommerceApp.Exceptions;

public class RootCategoryNotFoundException extends  RuntimeException{

    public RootCategoryNotFoundException(String message) {
        super(message);
    }
}
