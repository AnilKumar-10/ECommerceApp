package com.ECommerceApp.Exceptions.Product;

public class CategoryNotFoundException extends RuntimeException{
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
