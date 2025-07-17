package com.ECommerceApp.Model;

import lombok.Data;

@Data
public class OrderItem {
    private String productId;
    private String name;
    private int quantity;
    private String size;
    private String color;
    private double price;
    private String status;
    private double tax;
}

