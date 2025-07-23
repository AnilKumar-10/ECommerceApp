package com.ECommerceApp.DTO.User;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class ReviewCreationRequest {

    @Id
    private String id;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private int rating;

    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
}

