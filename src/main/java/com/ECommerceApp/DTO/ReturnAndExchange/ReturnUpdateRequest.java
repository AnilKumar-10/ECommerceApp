package com.ECommerceApp.DTO.ReturnAndExchange;

import lombok.Data;

@Data
public class ReturnUpdateRequest {
    private String deliveryPersonId;
    private boolean picked;
    private String orderId;
    private boolean anyDamage;
}
