package com.ECommerceApp.DTO;

import lombok.Data;

import java.util.Date;
@Data
public class RefundAndReturnResponseDTO {
    private String refundId;
    private String userId;       // Reference to the user who requested the refund
    private String orderId;      // Reference to the original order
    private String paymentId;    // Reference to the related payment
    private double refundAmount;

    private String reason;       // Reason for refund
    private String status;       // PENDING, APPROVED, REJECTED, COMPLETED
    private Date requestedAt;
    private Date processedAt;

    private String deliveryPersonName;
    private Date expectedPickUpDate;
    private boolean productPicked;
}
