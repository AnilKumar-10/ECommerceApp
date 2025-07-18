package com.ECommerceApp.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
//@Document
public class CartItem {
    private int  itemId;         // this field add after it is moved to cart.
    private String productId;
    private int quantity;
    private double price;
    private String size;
    private String color;
    private Date addedAt;
}

