package com.ECommerceApp.Model.User;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
public class RolePermission {
    private String role;

    private List<Permission> permissions;

    @Data
    public static class Permission {
        private String resource;
        private String action;
    }
}
