package com.ECommerceApp.Model.Payment;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class Invoice {
    @Id
    private String id;
    private String orderId;
    private String userId;
    private String paymentId;
    private String paymentMode;
    private double amount;
    private Date issuedAt;
}

