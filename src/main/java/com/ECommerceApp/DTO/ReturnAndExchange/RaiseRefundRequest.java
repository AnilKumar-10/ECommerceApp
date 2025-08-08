package com.ECommerceApp.DTO.ReturnAndExchange;

import lombok.Data;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class        RaiseRefundRequest {

    @NotBlank(message = "Order ID cannot be blank")
    private String orderId;

    @NotEmpty(message = "Product IDs list cannot be empty")
    private List<@NotBlank(message = "Product ID cannot be blank") String> productIds;

    @NotBlank(message = "Reason for refund cannot be blank")
    private String reason;
}

