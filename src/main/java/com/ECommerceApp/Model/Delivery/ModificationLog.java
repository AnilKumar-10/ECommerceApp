package com.ECommerceApp.Model.Delivery;

import lombok.Data;

import java.util.Date;

@Data
public class ModificationLog {
    private String field; // expectedDeliveryDate,status,address(these are the fields of shippingdetails class)
    private String updatedBy; // admin, user, seller , deliveryBoy
    private String oldValue;
    private String newValue;
    private Date modifiedAt;

}
