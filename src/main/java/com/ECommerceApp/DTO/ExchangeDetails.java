package com.ECommerceApp.DTO;

import lombok.Data;

@Data
public class ExchangeDetails {
    private String replacementProductId;
    private double replacementPrice;
    private double originalPrice;
    private double exchangeDifferenceAmount;
    private String paymentMode;
    private String paymentStatus;
    private String reason;
}
