package com.ECommerceApp.DTO.ReturnAndExchange;

import lombok.Data;

@Data
public class ExchangeInfo {
    private String orderId;
    private String productIdToPick;
    private String productIdToReplace;
    private double amount;
    private String amountPayType;
    private String paymentMode;

}
