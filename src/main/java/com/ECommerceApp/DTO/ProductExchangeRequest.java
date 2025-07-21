package com.ECommerceApp.DTO;

import lombok.Data;

@Data
public class ProductExchangeRequest {
    private String orderId;
    private String productIdToReplace;
    private String reasonToReplace;
    private String productId;
    private int quantity;
    private String size;
    private String color;
}
