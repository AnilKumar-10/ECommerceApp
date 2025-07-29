package com.ECommerceApp.DTO.Product;

import com.ECommerceApp.Model.Product.StockLog;
import com.ECommerceApp.Model.Product.StockLogModification;
import lombok.Data;

import java.util.Date;

import jakarta.validation.constraints.*;

@Data
public class StockLogModificationRequest {

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Seller ID is required")
    private String sellerId;

    @NotBlank(message = "Action is required")
    @Pattern(regexp = "ADD|SOLD|RETURNED", message = "Action must be one of: ADD, SOLD, RETURNED")
    private StockLogModification.ActionType action;

    @Min(value = 1, message = "Quantity changed must be at least 1")
    private int quantityChanged;

    @NotNull(message = "Modified date is required")
    private Date modifiedAt;
}
