package com.ECommerceApp.DTO.User;

import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String[] roles;               // "BUYER", "SELLER", "ADMIN"
    private String phone;
    private String gender;
    private boolean isActive;
}
