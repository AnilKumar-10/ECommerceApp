package com.ECommerceApp.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class Payment {
    @Id
    private String id;
    private String userId;
    private String orderId;
    private double amountPaid;
    private String paymentMethod;
    private String status;
    private Date transactionTime;
}

