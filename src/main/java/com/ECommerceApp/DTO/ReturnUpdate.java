package com.ECommerceApp.DTO;

import lombok.Data;

@Data
public class ReturnUpdate {
    private boolean picked;
    private String orderId;
    private boolean anyDamage;
}
