package com.ECommerceApp.DTO;

import lombok.Data;

@Data
public class RaiseRefundRequestDto {
    private String orderId;
    private String Reason;

}
