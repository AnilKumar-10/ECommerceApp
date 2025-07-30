package com.ECommerceApp.DTO.ReturnAndExchange;

import com.ECommerceApp.Model.Payment.Payment;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
@Data

public class ExchangeUpdateRequest {

    @NotBlank(message = "Order ID cannot be blank")
    private String orderId;

    @NotBlank(message = "Delivery person ID cannot be blank")
    private String deliveryPersonId;

    private String paymentId;

    @NotNull(message = "Exchanged status must be provided")
    private Boolean exchanged;

    @NotNull(message = "Damage information must be provided")
    private Boolean anyDamage;

    @NotNull(message = "Payment status must be provided")
    private Payment.PaymentStatus paymentStatus;
}
