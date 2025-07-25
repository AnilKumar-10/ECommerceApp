package com.ECommerceApp.DTO.ReturnAndExchange;

import lombok.Data;

@Data
public class     ProductExchangeRequest {
    private String orderId;
    private String productIdToReplace;
    private String reasonToReplace;
    private String newProductId;
    private int quantity;
    private String size;
    private String color;
}
