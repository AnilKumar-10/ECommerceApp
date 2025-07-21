package com.ECommerceApp.Model;

import com.ECommerceApp.DTO.DeliveryItems;
import com.ECommerceApp.DTO.ExchangeDeliveryItems;
import com.ECommerceApp.DTO.ProductReturnDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
public class DeliveryPerson {
    @Id
    private String id;
    private String name;
    private String phone;
    private boolean isActive;
    private int deliveredCount;
    private int toDeliveryCount;
    private List<String> assignedAreas; // Areas/zones they deliver to
    private List<DeliveryItems> toDeliveryItems;
    private List<ProductReturnDto> toReturnItems;
    private List<ExchangeDeliveryItems> toExchangeItems;
}

