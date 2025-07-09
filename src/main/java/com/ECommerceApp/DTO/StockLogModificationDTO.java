package com.ECommerceApp.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class StockLogModificationDTO {
    private String  productId;
    private String sellerId;
//    private String userId;
    private String action; // ADD,SOLD,RETURNED
    private int quantityChanged;
    private Date modifiedAt;
}
