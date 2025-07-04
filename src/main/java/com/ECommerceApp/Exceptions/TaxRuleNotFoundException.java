package com.ECommerceApp.Exceptions;

public class TaxRuleNotFoundException extends RuntimeException{

    public TaxRuleNotFoundException(String message) {
        super(message);
    }
}
