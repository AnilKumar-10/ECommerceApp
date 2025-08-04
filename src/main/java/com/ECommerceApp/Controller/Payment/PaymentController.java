package com.ECommerceApp.Controller.Payment;
import com.ECommerceApp.DTO.Payment.InitiatePaymentRequest;
import com.ECommerceApp.DTO.Payment.PaymentRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.ProductExchangeInfo;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.ServiceInterface.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController { //user

    @Autowired
    private IPaymentService paymentService;
    @Autowired
    private IReturnService returnService;
    @Autowired
    private IExchangeService exchangeService;

    //  BUYER / DELIVERY → make a new payment
    @PreAuthorize("hasPermission('PAYMENT', 'INSERT')")
    @PostMapping("/initPay")
    public Payment initiatePayment(@RequestBody InitiatePaymentRequest initiatePaymentDto) {
        return paymentService.initiatePayment(initiatePaymentDto);
    }

    //  BUYER / DELIVERY → finalize payment
    @PreAuthorize("hasPermission('PAYMENT', 'UPDATE')")
    @PostMapping("/pay")
    public Payment pay(@Valid @RequestBody PaymentRequest paymentDto) {
        return paymentDto.getStatus() == Payment.PaymentStatus.SUCCESS
                ? paymentService.confirmUPIPayment(paymentDto)
                : paymentService.failPayment(paymentDto);
    }

    //  ADMIN → get payment by ID
    @PreAuthorize("hasPermission('PAYMENT', 'READ')")
    @GetMapping("/getPayment/{paymentId}")
    public Payment getPaymentDetails(@PathVariable String paymentId) {
        return paymentService.getPaymentById(paymentId);
    }

    //  ADMIN → view all failed payments
    @PreAuthorize("hasPermission('PAYMENT', 'READ')")
    @GetMapping("/getAllFailedPayments")
    public List<Payment> getFailedPayments() {
        return paymentService.getAllFailedPayments();
    }

    //  BUYER / DELIVERY → exchange payment initiation
    @PreAuthorize("hasPermission('PAYMENT', 'INSERT')")
    @PostMapping("/initExchangePay")
    public Payment initiateExchangePay(@RequestBody InitiatePaymentRequest initiatePaymentDto) {
        return paymentService.initiateExchangePayment(initiatePaymentDto);
    }

    //  BUYER / DELIVERY → complete exchange payment
    @PreAuthorize("hasPermission('PAYMENT', 'UPDATE')")
    @PostMapping("/payExchange")
    public String upiPayExchangeAmount(@RequestBody PaymentRequest paymentDto) {
        if (paymentDto.getStatus() == Payment.PaymentStatus.SUCCESS) {
            Payment payment = paymentService.confirmUPIPaymentForExchange(paymentDto);
            exchangeService.processExchangeAfterUpiPayDone(payment.getOrderId(), payment.getId());
            return "Payment Successful";
        }
        return "Payment Failed! Please try again.";
    }

}
