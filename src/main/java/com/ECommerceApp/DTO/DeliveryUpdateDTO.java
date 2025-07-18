package com.ECommerceApp.DTO;

import lombok.Data;

@Data
public class    DeliveryUpdateDTO {
    private String orderId;
    private String paymentId; // for cod
    private String paymentStatus; // for cod
    private String shippingId;
    private String newValue; // delivered
    private String updateBy;

    /*
    for the cod
    we have to give the all the details
    because we have to update some fields in Order class like paymentId, transactionId, PaymentStatus.
    ====
    for UPI
    we just have to give orderId,shippingId,newValue,updateBy
     */
}
