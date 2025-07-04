package com.ECommerceApp.Model;

import lombok.Data;

import java.util.Date;

@Data
public class StockLogModification {
    private String userId;
    private String action; // ADD,SOLD
    private int quantityChanged;
    private Date modifiedAt;
}
