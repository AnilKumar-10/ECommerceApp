package com.ECommerceApp.DTO.User;

import com.ECommerceApp.Model.User.Users;
import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
        private String token;
        private String name;
        private String email;
        private String id;
        private List<Users.Role> roles;


}
