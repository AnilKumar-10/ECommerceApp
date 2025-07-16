package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.InitiatePaymentDto;
import com.ECommerceApp.DTO.PaymentDto;
import com.ECommerceApp.Model.Payment;
import com.ECommerceApp.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initUpiPay")
    public Payment initiatePaymentDto(@RequestBody InitiatePaymentDto initiatePaymentDto){
        return paymentService.initiatePayment(initiatePaymentDto);
    }

    @PostMapping("/initCodPay")
    public Payment initiateCODPay(@RequestBody InitiatePaymentDto initiatePaymentDto){
        return paymentService.initiatePayment(initiatePaymentDto);
    }

    @PostMapping("/pay")
    public Payment pay(@RequestBody PaymentDto paymentDto){
        return paymentDto.getStatus().equalsIgnoreCase("Success")?paymentService.confirmUPIPayment(paymentDto):paymentService.failPayment(paymentDto);
    }

    @GetMapping("/getPayment/{paymentId}")
    public Payment getPaymentDetails(@PathVariable String paymentId){
        return paymentService.getPaymentById(paymentId);
    }

    @GetMapping("/getAllFailedPayments")
    public List<Payment> getFailedPayments(){
        return paymentService.getAllFailedPayments();
    }
}
