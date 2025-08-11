package com.ECommerceApp.DTO.Order;

import lombok.Data;

@Data
public class OrderStatusCount {
    private String id;     // this maps to `_id` from aggregation
    private long total;    // count of orders
}

