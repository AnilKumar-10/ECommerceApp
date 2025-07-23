package com.ECommerceApp.DTO.User;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SellerResponse {
    private String id;
    private String name;
    private String email;
    private String[] roles;
    private String phone;
    private String gender;
    private boolean isActive;
    private Date createdAt;
    private Double rating;
    private String shopName;
    private String shopDescription;
    private List<String> shippingOptions;
    private List<String> assignedZones;
}
