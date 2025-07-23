package com.ECommerceApp.Model.Order;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class Coupon {
    @Id
    private String id;
    private String code;
    private String discountType; // "PERCENTAGE", "FLAT"
    private double discountValue;
    private double minOrderValue;
    private int maxUsagePerUser;
    private Date validFrom;
    private Date validTo;
    private boolean isActive = true;
}

