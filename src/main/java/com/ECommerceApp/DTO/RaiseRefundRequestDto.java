package com.ECommerceApp.DTO;

import lombok.Data;

import java.util.List;

@Data
public class RaiseRefundRequestDto {
    private String orderId;
    private List<String> productIds;
    private String reason;

}
