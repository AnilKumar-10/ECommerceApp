package com.ECommerceApp.Model.User;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
@Data
public class Cart {
    @Id
    private String id;
    private String buyerId;
    private List<CartItem> items;
    private double totalAmount;
    private Date updatedAt;
}
