package com.ECommerceApp.Exceptions;

import lombok.Data;
import org.springframework.data.mongodb.repository.Query;

public class PaymentAmountMissMatchException extends RuntimeException {
    public PaymentAmountMissMatchException(String message) {
        super(message);
    }
}
