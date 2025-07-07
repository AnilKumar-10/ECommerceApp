package com.ECommerceApp.DTO;

import lombok.Data;

@Data
public class InitiatePaymentDto {
    String orderId;
    String userId;
    double amount;
    String method;
}
