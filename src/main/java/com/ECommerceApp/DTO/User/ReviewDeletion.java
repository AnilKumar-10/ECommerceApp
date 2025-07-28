package com.ECommerceApp.DTO.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewDeletion {
    @NotBlank(message = "User ID is required")
    private String userId;
    @NotBlank(message = "Product ID is required")
    private String productId;
}
