package com.ECommerceApp.Model.User;

import lombok.Data;

import java.util.Date;
@Data
public class WishlistItem {
    private String productId;
    private String name;
    private boolean available;
    private int quantity;
    private double price;
    private String size;
    private String color;
    private Date addedAt;
}
