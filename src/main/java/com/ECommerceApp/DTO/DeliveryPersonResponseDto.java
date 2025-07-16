package com.ECommerceApp.DTO;

import lombok.Data;

import java.util.List;

@Data
public class DeliveryPersonResponseDto {
    private String id;
    private String name;
    private String phone;
    private boolean isActive;
    private List<DeliveryItems> toDeliveryItems;
}
