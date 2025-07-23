package com.ECommerceApp.Model.Order;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class TaxRule {
    @Id
    private String id;
    private String country;
    private String state;
    private String categoryId;
    private String categoryName;
    private double taxRate;
    private boolean isActive;
}

