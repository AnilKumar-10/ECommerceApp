package com.ECommerceApp.DTO.ReturnAndExchange;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReturnUpdateRequest {

    @NotBlank(message = "Delivery person ID cannot be blank")
    private String deliveryPersonId;

    @NotNull(message = "Picked status must be specified")
    private Boolean picked;

    @NotBlank(message = "Order ID cannot be blank")
    private String orderId;

    @NotNull(message = "Damage status must be specified")
    private Boolean anyDamage;

    private String damageType; // Damage, Already used, No tags
}
