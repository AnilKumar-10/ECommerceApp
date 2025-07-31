package com.ECommerceApp.DTO.Delivery;

import lombok.Data;

@Data
public class DeliveryPersonRegistrationResponse {

    private String id;
    private String name;
    private String phone;
    private String email;
    private boolean isActive;
}
