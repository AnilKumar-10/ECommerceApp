package com.ECommerceApp.DTO;

import lombok.Data;

import java.util.List;
@Data
public class OrderDto {
    List<String > productId;
    String userId;
    String Adrstype;
    String coupon;
    String payMode;
}
