package com.ECommerceApp.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
@Data
public class Order {
    @Id
    private String id;
    private String buyerId;
    private List<OrderItem> orderItems;
    private String addressId;
    private double totalAmount;
    private String couponId;
    private double discount;
    private double finalAmount;
    private String payMode;
    private String paymentId;
    private String status; // PLACED, CONFIRMED, PACKED, SHIPPED, IN_TRANSIT,OUT_FOR_DELIVERY, DELIVERED, CANCELLED, RETURN_REQUESTED, RETURNED, REFUNDED,
    private Date orderDate;
    private boolean isCancelled;
    private String cancelReason;
    private Date cancellationTime;
    private String shippingId;
}
