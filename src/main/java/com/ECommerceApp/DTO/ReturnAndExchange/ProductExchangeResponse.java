package com.ECommerceApp.DTO.ReturnAndExchange;

import lombok.Data;

import java.util.Date;

@Data
public class ProductExchangeResponse {

    private String orderId;
    private String productIdToPick;
    private String productIdToReplace;
    private double amount;
    private String amountPayType;
    private String deliveryPersonId;
    private String deliveryPersonName;
    private Date expectedReturnDate;


}
