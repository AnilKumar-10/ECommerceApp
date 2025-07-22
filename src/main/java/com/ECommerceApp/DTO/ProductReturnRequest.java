package com.ECommerceApp.DTO;

import com.ECommerceApp.Model.Address;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductReturnRequest {
    private String orderId;
    private List<String> productsId = new ArrayList<>();
    private List<String> productsName = new ArrayList<>();
    private String userName;
    private Address address;
    private String reason;
    private boolean productPicked;
}
