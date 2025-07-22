package com.ECommerceApp.DTO;

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
