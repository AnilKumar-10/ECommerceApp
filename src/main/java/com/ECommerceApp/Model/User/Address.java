package com.ECommerceApp.Model.User;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Address {
    @Id
    private String id;
    private String userId;
    private String phoneNo;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String type; // "HOME", "WORK"
}
