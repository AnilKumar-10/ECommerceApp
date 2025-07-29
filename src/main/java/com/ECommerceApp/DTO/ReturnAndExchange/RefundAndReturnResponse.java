package com.ECommerceApp.DTO.ReturnAndExchange;

import com.ECommerceApp.Model.RefundAndExchange.Refund;
import lombok.Data;

import java.util.Date;
@Data
public class RefundAndReturnResponse {
    private String refundId;
    private String userId;       // Reference to the user who requested the refund
    private String orderId;      // Reference to the original order
    private String paymentId;    // Reference to the related payment
    private double refundAmount;

    private String reason;       // Reason for refund
    private Refund.RefundStatus status;       // PENDING, APPROVED, REJECTED, COMPLETED
    private Date requestedAt;
    private Date processedAt;

    private String deliveryPersonName;
    private Date expectedPickUpDate;
    private boolean productPicked;
}
