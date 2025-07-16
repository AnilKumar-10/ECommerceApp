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
    private double tax;
    private double finalAmount;
    private String  refundId;
    private Double refundAmount;
    private String orderStatus;         // PLACED, CONFIRMED, PACKED, SHIPPED, IN_TRANSIT,OUT_FOR_DELIVERY, DELIVERED, CANCELLED, RETURN_REQUESTED, RETURNED, REFUNDED,
    private String paymentMethod;       // UPI, CARD, COD
    private String paymentStatus;       // PENDING, SUCCESS, FAILED
    private String paymentId;           // Link to Payment if online
    private Date orderDate;
    private boolean isCancelled;
    private String cancelReason;
    private Date cancellationTime;
    private boolean isReturned = false;
    private String shippingId;
}



