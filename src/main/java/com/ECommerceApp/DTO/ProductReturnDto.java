package com.ECommerceApp.DTO;

import com.ECommerceApp.Model.Address;
import lombok.Data;

@Data
public class ProductReturnDto {
    private String orderId;
    private String userName;
    private Address address;
    private String reason;
    private boolean productPicked;

}
