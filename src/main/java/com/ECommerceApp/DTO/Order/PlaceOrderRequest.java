package com.ECommerceApp.DTO.Order;

import lombok.Data;

import java.util.List;
@Data
public class PlaceOrderRequest {
    List<Integer > productIds;
    String userId;
    String addressType;
    String coupon;
    String payMode;
}
