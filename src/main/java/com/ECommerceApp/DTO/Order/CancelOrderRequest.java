package com.ECommerceApp.DTO.Order;

import lombok.Data;

@Data
public class CancelOrderRequest {
    private String orderId;
    private String reason;
}
