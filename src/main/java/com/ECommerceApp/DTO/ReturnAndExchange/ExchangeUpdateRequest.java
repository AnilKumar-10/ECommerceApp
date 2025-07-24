package com.ECommerceApp.DTO.ReturnAndExchange;

import lombok.Data;

@Data
public class ExchangeUpdateRequest {
    private String orderId;
    private String deliveryPersonId;
    private String paymentId;
    private boolean exchanged;
    private boolean anyDamage;
    private String paymentStatus;
}
