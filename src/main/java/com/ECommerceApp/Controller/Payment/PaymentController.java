package com.ECommerceApp.Controller.Payment;
import com.ECommerceApp.DTO.Payment.InitiatePaymentRequest;
import com.ECommerceApp.DTO.Payment.PaymentRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.ProductExchangeInfo;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.Service.ExchangeService;
import com.ECommerceApp.Service.PaymentService;
import com.ECommerceApp.Service.ReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PaymentController { //user

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ReturnService returnService;
    @Autowired
    private ExchangeService exchangeService;

    @PostMapping("/initPay")
    public Payment initiatePayment(@RequestBody InitiatePaymentRequest initiatePaymentDto){
        return paymentService.initiatePayment(initiatePaymentDto);
    }


    @PostMapping("/pay")
    public Payment pay(@RequestBody PaymentRequest paymentDto){
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


    @PostMapping("/initExchangePay")
    public Payment initiateExchangePay(@RequestBody InitiatePaymentRequest initiatePaymentDto){
        return paymentService.initiateExchangePayment(initiatePaymentDto);
    }

    @PostMapping("/payExchange")// for upi payment.
    public String  upiPayExchangeAmount(@RequestBody PaymentRequest paymentDto){
        if(paymentDto.getStatus().equalsIgnoreCase("SUCCESS")){
            Payment payment =  paymentService.confirmUPIPaymentForExchange(paymentDto);
            exchangeService.processExchangeAfterUpiPayDone(payment.getOrderId(),payment.getId());
            return "Payment Successful ";
        }
        return "Payment Failed.! , Please try again";
    }

    @PostMapping("/getExchangeInfo/{orderId}")
    public ProductExchangeInfo getExchangeInfor(@PathVariable String  orderId){
        return exchangeService.getExchangeInformation(orderId);
    }

}
