package com.ECommerceApp.DTO.Payment;

import com.ECommerceApp.Model.Payment.Payment;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class PaymentRequest {

    @NotBlank(message = "Payment ID must not be blank")
    private String paymentId;

    @NotBlank(message = "Transaction ID must not be blank")
    private String transactionId;

    @NotNull(message = "Status must not be null")
    private Payment.PaymentStatus status;
}
