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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
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
        log.info("inside the exchange update: "+exchangeUpdateRequest);
        exchangeService.exchangeUpdate(exchangeUpdateRequest);
        if(exchangeUpdateRequest.isExchanged() && exchangeUpdateRequest.getPaymentStatus().equalsIgnoreCase("SUCCESS")){
            return  ResponseEntity.ok(exchangeService.updateExchangeSuccess(exchangeUpdateRequest.getOrderId(),exchangeUpdateRequest.getDeliveryPersonId()));
        }
        return ResponseEntity.ok("Something went wrong. Please try again.");
    }

}
