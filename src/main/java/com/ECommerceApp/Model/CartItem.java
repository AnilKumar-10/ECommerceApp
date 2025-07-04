package com.ECommerceApp.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
//@Document
public class CartItem {
    private String productId;
    private int quantity;
    private double price;
    private String size;
    private String color;
    private Date addedAt;
}

