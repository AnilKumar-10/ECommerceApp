package com.ECommerceApp.Model.Delivery;

import com.ECommerceApp.Model.Order.Order;
import lombok.Data;

import java.util.Date;

@Data
public class ModificationLog {
    private String field; // expectedDeliveryDate,status,address(these are the fields of shippingdetails class)
    private String updatedBy; // admin, user, seller , deliveryBoy
    private Order.OrderStatus oldValue;
    private Order.OrderStatus newValue;
    private Date modifiedAt;

}
