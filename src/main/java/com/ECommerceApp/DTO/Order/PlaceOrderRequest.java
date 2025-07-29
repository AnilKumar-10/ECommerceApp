package com.ECommerceApp.DTO.Order;

import com.ECommerceApp.Model.Payment.Payment;
import lombok.Data;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PlaceOrderRequest {

    @NotEmpty(message = "Product list cannot be empty")
    private List<Integer> productIds;

    @NotBlank(message = "User ID must not be blank")
    private String userId;

    @NotBlank(message = "Address type must not be blank")
    private String addressType;

    private String coupon; // optional

    @NotNull(message = "Payment mode must not be null")
    private Payment.PaymentMethod payMode;
}
