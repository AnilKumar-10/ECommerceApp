package com.ECommerceApp.Controller.ReturnAndExchange;

import com.ECommerceApp.DTO.Delivery.DeliveryUpdate;
import com.ECommerceApp.DTO.Payment.PaymentRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.ExchangeDetails;
import com.ECommerceApp.DTO.ReturnAndExchange.ExchangeResponse;
import com.ECommerceApp.DTO.ReturnAndExchange.ExchangeUpdateRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.ProductExchangeRequest;
import com.ECommerceApp.Service.ExchangeService;
import com.ECommerceApp.Service.OrderService;
import com.ECommerceApp.Service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;


    @PostMapping("/RequestReturnExchange")
    public ExchangeResponse exchangeProduct(@RequestBody ProductExchangeRequest productExchangeDto){
        return exchangeService.exchangeRequest(productExchangeDto);
    }


    @PostMapping("/updateExchange")
    public ResponseEntity<?> exchangeUpdate(@RequestBody ExchangeUpdateRequest exchangeUpdateRequest){
        System.out.println("asd: "+exchangeUpdateRequest);
        ExchangeDetails exchangeDetails = orderService.getOrder(exchangeUpdateRequest.getOrderId()).getExchangeDetails();
        System.out.println("exchange: "+exchangeDetails);
        if(exchangeDetails.getExchangeType().equalsIgnoreCase("PAYABLE") && exchangeDetails.getPaymentMode().equalsIgnoreCase("COD") ){
            System.out.println("inside the if of update: "+exchangeUpdateRequest);
            PaymentRequest paymentDto = new PaymentRequest();
            paymentDto.setPaymentId(exchangeUpdateRequest.getPaymentId());
            paymentDto.setTransactionId(orderService.generateTransactionIdForCOD());
            paymentDto.setStatus("SUCCESS");
            paymentService.confirmCODPayment(paymentDto); // updating the payment success details
            exchangeService.markExchangeCodPaymentSuccess(exchangeUpdateRequest);// updating the order payment status
        }
        if(exchangeUpdateRequest.isExchanged() && exchangeUpdateRequest.getPaymentStatus().equalsIgnoreCase("SUCCESS")){
            System.out.println("inside");
            return  ResponseEntity.ok(exchangeService.updateExchangeSuccess(exchangeUpdateRequest.getOrderId(),exchangeUpdateRequest.getDeliveryPersonId()));
        }
        return ResponseEntity.ok("Something went wrong");
    }

}
