package com.ECommerceApp.DTO.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document
@Data
public class CategoryRequest {

    @Id
    private String id;

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    // parentId can be null or blank for root categories, so no @NotBlank here
    private String parentId;
}

