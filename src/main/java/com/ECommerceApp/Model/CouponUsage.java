package com.ECommerceApp.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class CouponUsage {
    @Id
    private String id;
    private String couponCode;
    private String userId;
    private Date usedAt;
}
