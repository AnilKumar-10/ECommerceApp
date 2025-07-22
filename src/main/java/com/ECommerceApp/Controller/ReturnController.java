package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.*;
import com.ECommerceApp.Model.Refund;
import com.ECommerceApp.Service.DeliveryService;
import com.ECommerceApp.Service.RefundService;
import com.ECommerceApp.Service.ReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReturnController {  // buyer

    @Autowired
    private RefundService refundService;
    @Autowired
    private ReturnService returnService;
    @Autowired
    private DeliveryService deliveryService;


    @PostMapping("/requestRefund")
    public RefundAndReturnResponse raiseRefundReq(@RequestBody RaiseRefundRequest refundRequestDto){
        return refundService.requestRefund(refundRequestDto);
    }


    @PostMapping("/updateReturn")
    public Refund updateReturn(@RequestBody ReturnUpdateRequest returnUpdate){
        if(returnUpdate.isPicked()){
            returnService.updateReturnSuccess(returnUpdate.getOrderId());
            return refundService.completeRefund(returnUpdate);
        }
        returnService.updateReturnFailed(returnUpdate.getOrderId());
        Refund refund = refundService.getRefundsByOrderId(returnUpdate.getOrderId());
        return refundService.rejectRefund(refund.getRefundId(),"Product Damaged.");
    }

    @PostMapping("/cancelOrder/{orderId}")
    public void cancelOrder(@PathVariable String orderId){
        refundService.cancelOrder(orderId,"Ordered by mistake");
    }

    @PostMapping("/RequestReturnExchange")
    public ExchangeInfo exchangeProduct(@RequestBody ProductExchangeRequest productExchangeDto){
        return returnService.exchangeRequest(productExchangeDto);
    }


    @PostMapping("/updateExchange")
    public ResponseEntity<?> exchangeUpdate(@RequestBody  ExchangeUpdateRequest exchangeUpdateRequest){
        System.out.println("asd: "+exchangeUpdateRequest);
        if(exchangeUpdateRequest.isExchanged()){
            System.out.println("inside");
            return  ResponseEntity.ok(returnService.updateExchangeSuccess(exchangeUpdateRequest.getOrderId(),exchangeUpdateRequest.getDeliveryPersonId()));
        }
        return ResponseEntity.ok("Something went wrong");
    }

}
