package com.ECommerceApp.DTO;

import com.ECommerceApp.Model.Address;
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

}
