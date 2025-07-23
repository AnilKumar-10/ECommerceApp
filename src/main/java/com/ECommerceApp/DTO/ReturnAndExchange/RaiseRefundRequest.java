package com.ECommerceApp.DTO.ReturnAndExchange;

import lombok.Data;

import java.util.List;

@Data
public class RaiseRefundRequest {
    private String orderId;
    private List<String> productIds;
    private String reason;

}
