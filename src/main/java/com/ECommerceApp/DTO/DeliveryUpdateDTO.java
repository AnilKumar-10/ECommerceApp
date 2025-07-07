package com.ECommerceApp.DTO;

import lombok.Data;

@Data
public class DeliveryUpdateDTO {
    private String orderId;
    private String paymentStatus;
    private String shippingId;
    private String newValue; // delivered
    private String updateBy;
}
