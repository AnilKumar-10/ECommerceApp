package com.ECommerceApp.DTO.Payment;

import lombok.Data;

@Data
public class InitiatePaymentRequest {
    String orderId;
    String userId;
    double amount;
    String method;
}
