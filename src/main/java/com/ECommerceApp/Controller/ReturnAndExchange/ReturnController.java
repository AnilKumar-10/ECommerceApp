package com.ECommerceApp.Controller.ReturnAndExchange;

import com.ECommerceApp.DTO.ReturnAndExchange.*;
import com.ECommerceApp.Model.RefundAndExchange.Refund;
import com.ECommerceApp.Service.DeliveryService;
import com.ECommerceApp.Service.RefundService;
import com.ECommerceApp.Service.ReturnService;
import org.springframework.beans.factory.annotation.Autowired;
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



}
