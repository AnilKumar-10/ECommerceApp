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
    private String returnPolicy;
    private int returnBefore; // no of days like 5,10
    private boolean isReturnable;
    private List<String> categoryIds;       // Reference to Categories collection
    private String sellerId;         // Reference to Users collection (with role = SELLER)

    private List<String> colors;
    private List<String> sizes;
    private List<String> images;

    private double rating; // Average rating (computed from reviews collection)
    private boolean isAvailable;
    private Date addedOn;

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
