package com.ECommerceApp.Model.Delivery;

import com.ECommerceApp.DTO.Delivery.DeliveryItems;
import com.ECommerceApp.DTO.ReturnAndExchange.ExchangeDeliveryItems;
import com.ECommerceApp.DTO.ReturnAndExchange.ProductReturnDetails;
import com.ECommerceApp.Model.User.Users;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
@Data
public class DeliveryPerson {
    @Id
    private String id;
    private String name;
    private String phone;
    private String email;
    private String password;
    private List<Users.Role> roles;
    private boolean isActive = true;
    private Date passwordChangedAt;
    private int deliveredCount =0;
    private int toDeliveryCount=0;
    private List<String> assignedAreas = new ArrayList<>(); // Areas/zones they deliver to
    private List<DeliveryItems> toDeliveryItems = new ArrayList<>();
    private List<ProductReturnDetails> toReturnItems = new ArrayList<>();
    private List<ExchangeDeliveryItems> toExchangeItems = new ArrayList<>();
}

