package com.ECommerceApp.Model;

import lombok.Data;

import java.util.Date;
@Data
public class WishlistItem {
    private String productId;              // Refers to PRODUCTS._id
    private Date addedAt;
}
