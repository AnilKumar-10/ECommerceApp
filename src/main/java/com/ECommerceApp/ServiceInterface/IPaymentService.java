package com.ECommerceApp.ServiceInterface;

import com.ECommerceApp.DTO.Payment.InitiatePaymentRequest;
import com.ECommerceApp.DTO.Payment.PaymentRequest;
import com.ECommerceApp.Model.Payment.Payment;

import java.util.List;

public interface IPaymentService {

    Payment initiatePayment(InitiatePaymentRequest initiatePaymentDto);

    Payment confirmUPIPayment(PaymentRequest paymentDto);

    Payment failPayment(PaymentRequest paymentDto);

    void confirmCODPayment(PaymentRequest paymentDto);

    Payment getPaymentById(String paymentId);

    List<Payment> getAllFailedPayments();

    Payment savePayment(Payment payment);

    Payment initiateExchangePayment(InitiatePaymentRequest initiatePaymentDto);

    Payment confirmUPIPaymentForExchange(PaymentRequest paymentDto);
}
