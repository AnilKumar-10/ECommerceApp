package com.ECommerceApp.DTO;

import lombok.Data;

@Data
public class ExchangeUpdateRequest {
    private String deliveryPersonId;
    private boolean exchanged;
    private String orderId;
    private boolean anyDamage;
}
