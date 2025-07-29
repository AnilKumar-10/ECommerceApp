package com.ECommerceApp.Model.Delivery;

import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Model.User.Address;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
@Data
public class ShippingDetails {
    @Id
    private String id;
    private String orderId;
    private String courierName ;
    private Address DeliveryAddress;
    private String trackingId;
    private Date expectedDate;
    private Order.OrderStatus status; // PLACED, CONFIRMED, PACKED, SHIPPED, IN_TRANSIT,OUT_FOR_DELIVERY, DELIVERED, CANCELLED, RETURN_REQUESTED, RETURNED, REFUNDED,
    private String deliveryPersonId;
    private List<ModificationLog> modificationLogs;
}
