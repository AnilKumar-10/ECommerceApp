package com.ECommerceApp.Exceptions.Product;

public class ReviewAlreadyExistsException extends RuntimeException{
    public ReviewAlreadyExistsException(String message) {
        super(message);
    }
}
