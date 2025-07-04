package com.ECommerceApp.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class Review {
    @Id
    private String id;
    private String productId;
    private String userId;
    private int rating;
    private String comment;
    private Date createdAt;
    private Date updatedAt;
    private boolean isVerifiedBuyer = false;
}

