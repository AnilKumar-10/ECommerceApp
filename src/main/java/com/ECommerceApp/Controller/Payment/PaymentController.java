package com.ECommerceApp.Controller.Payment;
import com.ECommerceApp.DTO.Payment.InitiatePaymentRequest;
import com.ECommerceApp.DTO.Payment.PaymentRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.ProductExchangeInfo;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.ServiceInterface.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PaymentController { //user

    @Autowired
    private IPaymentService paymentService;
    @Autowired
    private IReturnService returnService;
    @Autowired
    private IExchangeService exchangeService;

    @PostMapping("/initPay")
    public Payment initiatePayment(@RequestBody InitiatePaymentRequest initiatePaymentDto){
        return paymentService.initiatePayment(initiatePaymentDto);
    }


    @PostMapping("/pay")
    public Payment pay(@Valid @RequestBody PaymentRequest paymentDto) {
        return paymentDto.getStatus() == Payment.PaymentStatus.SUCCESS
                ? paymentService.confirmUPIPayment(paymentDto)
                : paymentService.failPayment(paymentDto);
    }


    @GetMapping("/getPayment/{paymentId}")
    public Payment getPaymentDetails(@PathVariable String paymentId){
        return paymentService.getPaymentById(paymentId);
    }

    @GetMapping("/getAllFailedPayments")
    public List<Payment> getFailedPayments(){
        return paymentService.getAllFailedPayments();
    }


    @PostMapping("/initExchangePay")
    public Payment initiateExchangePay(@RequestBody InitiatePaymentRequest initiatePaymentDto){
        return paymentService.initiateExchangePayment(initiatePaymentDto);
    }

    @PostMapping("/payExchange")// for upi payment.
    public String  upiPayExchangeAmount(@RequestBody PaymentRequest paymentDto){
        if(paymentDto.getStatus() == Payment.PaymentStatus.SUCCESS){
            Payment payment =  paymentService.confirmUPIPaymentForExchange(paymentDto);
            exchangeService.processExchangeAfterUpiPayDone(payment.getOrderId(),payment.getId());
            return "Payment Successful ";
        }
        return "Payment Failed.! , Please try again";
    }



}
