package com.ECommerceApp.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "At least one role is required")
    @Size(min = 1, message = "At least one role must be specified")
    private String[] roles;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String gender;

    private boolean isActive = true;

    private Date createdAt;

    // Seller-specific fields (nullable if role is not SELLER)
    private Double rating;

    private String shopName;

    private String shopDescription;

    private List<String> shippingOptions;

    private List<String> assignedZones;
}
