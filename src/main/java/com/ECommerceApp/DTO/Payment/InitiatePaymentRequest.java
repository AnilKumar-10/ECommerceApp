package com.ECommerceApp.DTO.Payment;

import com.ECommerceApp.Model.Payment.Payment;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class InitiatePaymentRequest {
    @NotNull
    private String orderId;
    @NotNull
    private String userId;
    @NotNull
    private double amount;
    @NotNull
    private Payment.PaymentMethod method;
}

