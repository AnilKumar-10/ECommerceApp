package com.ECommerceApp.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class ProductExchangeResponse {

    private String orderId;
    private String productIdToPick;
    private String productIdToReplace;
    private double amount;
    private String deliveryPersonId;
    private String deliveryPersonName;
    private Date expectedReturnDate;


}
