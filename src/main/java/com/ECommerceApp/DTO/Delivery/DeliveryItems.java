package com.ECommerceApp.DTO.Delivery;

import com.ECommerceApp.Model.User.Address;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DeliveryItems {

    @NotBlank(message = "Shipping ID cannot be blank")
    private String shippingId;

    @NotBlank(message = "Order ID cannot be blank")
    private String orderId;

    @NotBlank(message = "User name cannot be blank")
    private String userName;

    @NotNull(message = "Address must not be null")
    private Address address;

    @NotBlank(message = "Payment mode cannot be blank")
    private String paymentMode;

    @Positive(message = "Amount to pay must be greater than zero")
    private double amountToPay;
}
