package com.ECommerceApp.DTO.Payment;

import lombok.Data;

@Data
public class PaymentRequest {
    String paymentId;
    String transactionId;
    String status;
}
