package com.ECommerceApp.Model.User;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document
public class Users {

    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String[] roles;               // "BUYER", "SELLER", "ADMIN"
    private String phone;
    private String gender;
    private boolean isActive;
    private Date createdAt;
    private String upiId;

    // Seller-specific fields (nullable for non-sellers)
    private Double rating;
    private String shopName;
    private String shopDescription;
    private List<String> shippingOptions;
    private List<String> assignedZones;

}
