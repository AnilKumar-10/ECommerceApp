package com.ECommerceApp.Model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class DeliveryHistory {

    private String deliveryHistoryId;
    private String deliverPersonId;
//    private
}
