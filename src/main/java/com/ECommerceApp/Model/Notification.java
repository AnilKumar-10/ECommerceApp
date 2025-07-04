package com.ECommerceApp.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class Notification {
    @Id
    private String id;
    private String userId;
    private String message;
    private String type; // "ORDER", "PROMO","Delivery" etc.
    private boolean isRead;
    private Date createdAt;
}
