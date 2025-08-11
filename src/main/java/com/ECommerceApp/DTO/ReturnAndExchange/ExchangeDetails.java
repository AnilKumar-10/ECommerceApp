package com.ECommerceApp.DTO.ReturnAndExchange;

import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.Model.RefundAndExchange.Refund;
import lombok.Data;

import java.util.Date;

import lombok.Data;

import java.util.Date;

@Data
public class ExchangeDetails {

    private String replacementProductId;

    private double replacementPrice;     // New product price
    private double originalPrice;        // Original product price

    private double exchangeDifferenceAmount;
    private ExchangeType exchangeType;

    private String paymentId;
    private Payment.PaymentMethod paymentMode;     // UPI, COD (if PAYABLE)
    private Payment.PaymentStatus paymentStatus;   // PENDING, SUCCESS, FAILED (if PAYABLE)

    private String refundId;
    private RefundMode refundMode;                 // UPI (if REFUNDABLE)
    private Refund.RefundStatus refundStatus;      // PENDING, COMPLETED, FAILED (if REFUNDABLE)
    private String reason;                         // Reason for exchange
    private Date createdAt;
    private Date updatedAt;


    public enum ExchangeType {
        PAYABLE,
        REFUNDABLE,
        NO_DIFFERENCE
    }


    public enum RefundMode {
        UPI
    }


}
