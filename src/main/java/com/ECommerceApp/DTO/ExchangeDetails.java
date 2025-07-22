package com.ECommerceApp.DTO;

import lombok.Data;

import java.util.Date;

//@Data
//public class ExchangeDetails {
//    private String replacementProductId;
//    private double replacementPrice;
//    private double originalPrice;
//    private double exchangeDifferenceAmount;
//    private String paymentMode;
//    private String paymentStatus;
//    private String reason;
//}
@Data
public class ExchangeDetails {

    private String replacementProductId;

    private double replacementPrice;     // New product price
    private double originalPrice;        // Original product price

    private double exchangeDifferenceAmount;

    // "PAYABLE", "REFUNDABLE", or "NO_DIFFERENCE"
    private String exchangeType;


    private String paymentMode;          // UPI, COD (if PAYABLE)
    private String paymentStatus;        // PENDING, SUCCESS, FAILED (if PAYABLE)

    private String refundMode;           // UPI (if REFUNDABLE)
    private String refundStatus;         // PENDING, COMPLETED, FAILED (if REFUNDABLE)

    private String reason;               // Reason for exchange

    private Date createdAt;
    private Date updatedAt;
}
