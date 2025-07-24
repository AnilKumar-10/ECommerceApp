package com.ECommerceApp.DTO.ReturnAndExchange;

import com.ECommerceApp.Model.User.Address;
import lombok.Data;


@Data
public class ExchangeDeliveryItems {

    private String orderId;
    private String userName;
    private String productIdToPick;
    private String productIdToReplace;
    private double amount;
    private Address address;
    private String paymentMode;
    private boolean payable;
}
