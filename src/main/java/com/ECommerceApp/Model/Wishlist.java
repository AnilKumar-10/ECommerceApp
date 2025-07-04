package com.ECommerceApp.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
@Data
@Document
public class Wishlist {
    @Id
    private String id;
    private String buyerId;                 // Refers to USERS._id
    private List<WishlistItem> items;       // Embedded list of product references
    private Date updatedAt;
}

