package com.ECommerceApp.DTO;

//package com.ECommerceApp.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
@Component
@Data
public class ProductRequest {

    private String id;

    @NotBlank(message = "Product name is required")
    private String name;

    @Size(max = 500, message = "Description must be under 500 characters")
    private String description;

    @Positive(message = "Price must be greater than zero")
    private double price;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    @NotBlank(message = "Return policy must be provided")
    private String returnPolicy;

    @NotBlank(message = "Category ID is required")
    private List<String> categoryIds;

    @NotBlank(message = "Seller ID is required")
    private String sellerId;

    private List<@NotBlank(message = "Color cannot be blank") String> colors;

    private List<@NotBlank(message = "Size cannot be blank") String> sizes;

    private List<@NotBlank(message = "Image URL cannot be blank") String> images;

    // These fields are usually not input by client
    private boolean isAvailable = true;

    private Date addedOn = new Date(); // Optional: can be set automatically in service
    private int returnBefore;
    private  boolean returnable;
    // Getters and Setters
}
