package com.ECommerceApp.DTO.Delivery;

import lombok.Data;

import java.util.List;

@Data
public class DeliveryPersonResponse {
    private String id;
    private String name;
    private String phone;
    private boolean isActive;
    private List<DeliveryItems> toDeliveryItems;
}
