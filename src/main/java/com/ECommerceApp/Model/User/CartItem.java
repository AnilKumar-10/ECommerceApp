package com.ECommerceApp.Model.User;

import lombok.Data;

import java.util.Date;

@Data
//@Document
public class CartItem {
    private int  itemId;         // this field add after it is moved to cart.
    private String productId;
    private String name;
    private int quantity;
    private double price;
    private String size;
    private String color;
    private Date addedAt;
}

