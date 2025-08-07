package com.ECommerceApp.Controller.Payment;
import com.ECommerceApp.DTO.Payment.InitiatePaymentRequest;
import com.ECommerceApp.DTO.Payment.PaymentRequest;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.ServiceInterface.Order.IExchangeService;
import com.ECommerceApp.ServiceInterface.Order.IReturnService;
import com.ECommerceApp.ServiceInterface.Payment.IPaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> initiatePayment(@RequestBody InitiatePaymentRequest initiatePaymentDto) {
        return ResponseEntity.ok(paymentService.initiatePayment(initiatePaymentDto));
    }

    //  BUYER / DELIVERY → finalize payment
    @PreAuthorize("hasPermission('PAYMENT', 'INSERT')")
    @PostMapping("/pay")
    public ResponseEntity<?> pay(@Valid @RequestBody PaymentRequest paymentDto) {
        return ResponseEntity.ok(paymentDto.getStatus() == Payment.PaymentStatus.SUCCESS
                ? paymentService.confirmUPIPayment(paymentDto)
                : paymentService.failPayment(paymentDto));
    }

    //  ADMIN → get payment by ID
    @PreAuthorize("hasPermission('PAYMENT', 'READ')")
    @GetMapping("/getPayment/{paymentId}")
    public ResponseEntity<?> getPaymentDetails(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    //  ADMIN → view all failed payments
    @PreAuthorize("hasPermission('PAYMENT', 'READ')")
    @GetMapping("/getAllFailedPayments")
    public ResponseEntity<?> getFailedPayments() {
        return ResponseEntity.ok(paymentService.getAllFailedPayments());
    }

    //  BUYER / DELIVERY → exchange payment initiation
    @PreAuthorize("hasPermission('PAYMENT', 'INSERT')")
    @PostMapping("/initExchangePay")
    public ResponseEntity<?> initiateExchangePay(@RequestBody InitiatePaymentRequest initiatePaymentDto) {
        return ResponseEntity.ok(paymentService.initiateExchangePayment(initiatePaymentDto));
    }

    //  BUYER / DELIVERY → complete exchange payment
    @PreAuthorize("hasPermission('PAYMENT', 'INSERT')")
    @PostMapping("/payExchange")
    public ResponseEntity<?> upiPayExchangeAmount(@RequestBody PaymentRequest paymentDto) {
        if (paymentDto.getStatus() == Payment.PaymentStatus.SUCCESS) {
            Payment payment = paymentService.confirmUPIPaymentForExchange(paymentDto);
            exchangeService.processExchangeAfterUpiPayDone(payment.getOrderId(), payment.getId());
            return ResponseEntity.ok("Payment Successful");
        }
        return ResponseEntity.ok("Payment Failed! Please try again.");
    }

}
