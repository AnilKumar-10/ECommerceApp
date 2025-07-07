package com.ECommerceApp.Model;

import lombok.Data;

@Data
public class OrderItem {
    private String productId;
    private int quantity;
    private String size;
    private String color;
    private double price;
}

