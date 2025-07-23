package com.ECommerceApp.Controller.ReturnAndExchange;

import com.ECommerceApp.DTO.ReturnAndExchange.ExchangeInfo;
import com.ECommerceApp.DTO.ReturnAndExchange.ExchangeUpdateRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.ProductExchangeRequest;
import com.ECommerceApp.Service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @PostMapping("/RequestReturnExchange")
    public ExchangeInfo exchangeProduct(@RequestBody ProductExchangeRequest productExchangeDto){
        return exchangeService.exchangeRequest(productExchangeDto);
    }


    @PostMapping("/updateExchange")
    public ResponseEntity<?> exchangeUpdate(@RequestBody ExchangeUpdateRequest exchangeUpdateRequest){
        System.out.println("asd: "+exchangeUpdateRequest);
        if(exchangeUpdateRequest.isExchanged()){
            System.out.println("inside");
            return  ResponseEntity.ok(exchangeService.updateExchangeSuccess(exchangeUpdateRequest.getOrderId(),exchangeUpdateRequest.getDeliveryPersonId()));
        }
        return ResponseEntity.ok("Something went wrong");
    }
}
