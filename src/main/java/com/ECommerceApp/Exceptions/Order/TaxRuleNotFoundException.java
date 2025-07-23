package com.ECommerceApp.Exceptions.Order;

public class TaxRuleNotFoundException extends RuntimeException{

    public TaxRuleNotFoundException(String message) {
        super(message);
    }
}
