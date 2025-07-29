package com.ECommerceApp.DTO.ReturnAndExchange;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class ProductExchangeRequest {

    @NotBlank(message = "Order ID cannot be blank")
    private String orderId;

    @NotBlank(message = "Product ID to replace cannot be blank")
    private String productIdToReplace;

    @NotBlank(message = "Reason to replace cannot be blank")
    private String reasonToReplace;

    @NotBlank(message = "New product ID cannot be blank")
    private String newProductId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

    @NotBlank(message = "Size cannot be blank")
    private String size;

    @NotBlank(message = "Color cannot be blank")
    private String color;
}
