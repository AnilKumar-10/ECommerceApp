package com.ECommerceApp.Exceptions.Product;

public class ProductOutOfStockException extends RuntimeException{
    public ProductOutOfStockException(String message) {
        super(message);
    }
}
