package com.ECommerceApp.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document
public class Product {

    @Id
    private String id;
    private String name;
    private String description;
    private double price;
    private int stock;
    String returnPolicy;
    private String categoryId;       // Reference to Categories collection
    private String sellerId;         // Reference to Users collection (with role = SELLER)

    private List<String> colors;
    private List<String> sizes;
    private List<String> images;

    private double rating; // Average rating (computed from reviews collection)
    private boolean isAvailable;
    private Date addedOn;
}
