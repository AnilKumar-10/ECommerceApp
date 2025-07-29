package com.ECommerceApp.DTO.Delivery;

import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Model.Payment.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class DeliveryUpdate {

    @NotBlank(message = "Order ID must not be blank")
    private String orderId;

    // These fields are only required for COD
    private String paymentId;

    private Payment.PaymentStatus paymentStatus;

    @NotBlank(message = "Shipping ID must not be blank")
    private String shippingId;

    @NotNull(message = "New value must not be blank")
    private Order.OrderStatus newValue;

    @NotBlank(message = "UpdateBy must not be blank")
    private String updateBy;
}
