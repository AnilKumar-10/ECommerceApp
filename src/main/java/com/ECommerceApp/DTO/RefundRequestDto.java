package com.ECommerceApp.DTO;

import lombok.Data;

@Data
public class RefundRequestDto {
    private String orderId;
    private String Reason;
}
