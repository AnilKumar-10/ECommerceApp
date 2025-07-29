package com.ECommerceApp.Model.Payment;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class Payment {
    @Id
    private String id;
    private String transactionId;
    private String userId;
    private String orderId;
    private double amountPaid;
    private PaymentMethod paymentMethod;   // Enum inside Payment
    private PaymentStatus status;          // Enum inside Payment
    private Date transactionTime;

    public enum PaymentMethod {
        UPI,
        COD
    }

    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED
    }
}

