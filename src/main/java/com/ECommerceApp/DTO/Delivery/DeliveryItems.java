package com.ECommerceApp.DTO.Delivery;

import com.ECommerceApp.Model.User.Address;
import lombok.Data;

@Data
public class DeliveryItems {
    private String shippingId;
    private String orderId;
    private String userName;
    private Address address;
    private String paymentMode;
    private double amountToPay;
}
