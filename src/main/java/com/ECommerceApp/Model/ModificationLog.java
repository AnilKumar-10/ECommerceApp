package com.ECommerceApp.Model;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class ModificationLog {
    private String field; // expectedDeliveryDate,status,address(these are the fields of shippingdetails class)
    private String updatedBy; // admin, user, seller , deliveryBoy
    private String oldValue;
    private String newValue;
    private Date modifiedAt;

}
