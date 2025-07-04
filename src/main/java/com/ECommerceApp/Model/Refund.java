package com.ECommerceApp.Model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class    Refund {

    @Id
    private String id;

    private String userId;       // Reference to the user who requested the refund
    private String orderId;      // Reference to the original order
    private String paymentId;    // Reference to the related payment
    private double refundAmount;

    private String reason;       // Reason for refund
    private String status;       // PENDING, APPROVED, REJECTED, COMPLETED
    private Date requestedAt;
    private Date processedAt;
}
