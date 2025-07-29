package com.ECommerceApp.DTO.Order;

import com.ECommerceApp.Model.Order.Order;
import lombok.Data;

@Data
public class ShippingUpdateRequest {
    private String shippingId;
    private Order.OrderStatus newValue;
    private String updateBy;
}
