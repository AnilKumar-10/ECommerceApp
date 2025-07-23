package com.ECommerceApp.Model.Product;

import lombok.Data;

import java.util.Date;

@Data
public class StockLogModification {
//    private String userId;
    private String action; // ADD,SOLD,RETURNED
    private int quantityChanged;
    private Date modifiedAt;
}
