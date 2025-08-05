package com.ECommerceApp.Controller.ReturnAndExchange;

import com.ECommerceApp.DTO.ReturnAndExchange.ExchangeResponse;
import com.ECommerceApp.DTO.ReturnAndExchange.ExchangeUpdateRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.ProductExchangeInfo;
import com.ECommerceApp.DTO.ReturnAndExchange.ProductExchangeRequest;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.ServiceInterface.Order.IExchangeService;
import com.ECommerceApp.ServiceInterface.Order.IOrderService;
import com.ECommerceApp.ServiceInterface.Payment.IPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/exchange")
public class ExchangeController {

    @Autowired
    private IExchangeService exchangeService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IPaymentService paymentService;


    //  BUYER: Request an exchange
    @PreAuthorize("hasPermission('EXCHANGE', 'INSERT')")
    @PostMapping("/RequestReturnExchange")
    public ExchangeResponse exchangeProduct(@RequestBody ProductExchangeRequest productExchangeDto){
        return exchangeService.exchangeRequest(productExchangeDto);
    }

    //  BUYER: View exchange info for own order
    @PreAuthorize("hasPermission('EXCHANGE', 'READ')")
    @PostMapping("/getExchangeInfo/{orderId}")
    public ProductExchangeInfo getExchangeInformation(@PathVariable String orderId){
        return exchangeService.getExchangeInformation(orderId);
    }

    //  DELIVERY: Update exchange status
    @PreAuthorize("hasPermission('EXCHANGE', 'UPDATE')")
    @PostMapping("/updateExchange")
    public ResponseEntity<?> exchangeUpdate(@RequestBody ExchangeUpdateRequest exchangeUpdateRequest){
        log.info("inside the exchange update: "+exchangeUpdateRequest);
        exchangeService.exchangeUpdate(exchangeUpdateRequest);
        if(exchangeUpdateRequest.getExchanged() && exchangeUpdateRequest.getPaymentStatus() == Payment.PaymentStatus.SUCCESS ){
            return  ResponseEntity.ok(exchangeService.updateExchangeSuccess(exchangeUpdateRequest.getOrderId(),exchangeUpdateRequest.getDeliveryPersonId()));
        }
        return ResponseEntity.ok("Something went wrong. Please try again.");
    }

}
