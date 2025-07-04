package com.ECommerceApp.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class TaxRule {
    @Id
    private String id;
    private String country;
    private String state;
    private String categoryId;
    private double taxRate;
    private boolean isActive;
}

