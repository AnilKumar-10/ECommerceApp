package com.ECommerceApp.DTO;

import com.ECommerceApp.Model.Address;
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
