package com.ECommerceApp.DTO;

import lombok.Data;

@Data
public class PaymentDto {
    String paymentId;
    String transactionId;
    String status;
}
