package com.ECommerceApp.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
@Data
public class StockLog {
    @Id
    private String id;
    private String productId;
    private String sellerId;
    private List<StockLogModification> logModification;
    private int currentQuantity;
    private Date timestamp;
}
