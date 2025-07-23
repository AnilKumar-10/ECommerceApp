package com.ECommerceApp.Exceptions.Product;

public class RootCategoryNotFoundException extends  RuntimeException{

    public RootCategoryNotFoundException(String message) {
        super(message);
    }
}
