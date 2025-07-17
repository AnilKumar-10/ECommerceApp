package com.ECommerceApp.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class DeliveryHistory {

    @Id
    private String id;
    private String deliverPersonId;
    private String name;
    private String shippingId;
    private String OrderId;
    private String userName;
    private Address address;
    private String paymentMode;
    private double amountToPay;

}
