package com.ECommerceApp.DTO;

import lombok.Data;

@Data
public class ShippingUpdateDTO {
    private String shippingId;
    private String newValue;
    private String updateBy;
}
