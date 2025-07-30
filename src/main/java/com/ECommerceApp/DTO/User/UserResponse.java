package com.ECommerceApp.DTO.User;

import com.ECommerceApp.Model.User.Users;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private List<Users.Role> roles;               // "BUYER", "SELLER", "ADMIN"
    private String phone;
    private String gender;
    private boolean isActive;
}
