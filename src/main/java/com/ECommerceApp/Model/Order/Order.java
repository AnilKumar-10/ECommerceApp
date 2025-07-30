package com.ECommerceApp.Model.Order;

import com.ECommerceApp.DTO.ReturnAndExchange.ExchangeDetails;
import com.ECommerceApp.Model.Payment.Payment;
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
    private OrderStatus orderStatus;         // PENDING, PLACED,SHIPPED,OUT_FOR_DELIVERY, DELIVERED, CANCELLED, REQUESTED_TO_RETURN, RETURNED, REFUNDED, REQUESTED_TO_EXCHANGE, EXCHANGED
    private Payment.PaymentMethod paymentMethod;       // UPI, COD
    private Payment.PaymentStatus paymentStatus;       // PENDING, SUCCESS, FAILED
    private String paymentId;
    private Date orderDate;
    private boolean isCancelled;
    private String cancelReason;
    private Date cancellationTime;
    private boolean isReturned = false;
    private String shippingId;


    private String upiId;
    private ExchangeDetails exchangeDetails;



    public enum OrderStatus {
        PENDING,
        PLACED,
        SHIPPED,
        OUT_FOR_DELIVERY,
        DELIVERED,
        CANCELLED,
        REQUESTED_TO_RETURN,
        RETURNED,
        REQUESTED_TO_EXCHANGE,
        EXCHANGED,
        RETURN_FAILED,
        EXCHANGE_FAILED,
        TO_DELIVER,
        EXCHANGE_RETURNED,
        EXCHANGE_DELIVERED
    }


}



