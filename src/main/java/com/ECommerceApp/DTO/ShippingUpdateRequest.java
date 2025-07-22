package com.ECommerceApp.DTO;

import lombok.Data;

@Data
public class ShippingUpdateRequest {
    private String shippingId;
    private String newValue;
    private String updateBy;
}
