package com.ECommerceApp.DTO;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class TaxRuleRequestDTO {

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax rate must be non-negative")
    @DecimalMax(value = "100.0", inclusive = true, message = "Tax rate cannot exceed 100%")
    private double taxRate;

    private boolean isActive = true; // default true
}
