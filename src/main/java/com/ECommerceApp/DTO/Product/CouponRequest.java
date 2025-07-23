package com.ECommerceApp.DTO.Product;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class CouponRequest {

    @Id
    private String id;

    @NotBlank(message = "Coupon code is required")
    @Size(max = 30, message = "Coupon code must not exceed 30 characters")
    private String code;

    @NotBlank(message = "Discount type is required")
    @Pattern(regexp = "PERCENTAGE|FLAT", message = "Discount type must be either 'PERCENTAGE' or 'FLAT'")
    private String discountType;

    @Positive(message = "Discount value must be greater than 0")
    private double discountValue;

    @PositiveOrZero(message = "Minimum order value must be 0 or greater")
    private double minOrderValue;

    @Min(value = 1, message = "Max usage per user must be at least 1")
    private int maxUsagePerUser;

    @NotNull(message = "Valid from date is required")
    private Date validFrom;

    @NotNull(message = "Valid to date is required")
    @Future(message = "Valid to date must be in the future")
    private Date validTo;

    private boolean isActive = true;
}

