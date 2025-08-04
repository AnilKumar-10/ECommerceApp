package com.ECommerceApp.Controller.ReturnAndExchange;

import com.ECommerceApp.DTO.ReturnAndExchange.*;
import com.ECommerceApp.Model.RefundAndExchange.Refund;
import com.ECommerceApp.ServiceInterface.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/return")
public class ReturnController {  // buyer

    @Autowired
    private IRefundService refundService;
    @Autowired
    private IReturnService returnService;
    @Autowired
    private IDeliveryService deliveryService;


    // BUYER: Raise refund request
    @PreAuthorize("hasPermission('RETURN', 'INSERT')")
    @PostMapping("/requestRefund")
    public RefundAndReturnResponse raiseRefundReq(@RequestBody RaiseRefundRequest refundRequestDto){
        return refundService.requestRefund(refundRequestDto);
    }

    //  DELIVERY: Update return status
    @PreAuthorize("hasPermission('RETURN', 'UPDATE')")
    @PostMapping("/updateReturn")
    public Refund updateReturn(@RequestBody ReturnUpdateRequest returnUpdate){
        if(returnUpdate.getPicked()){
            returnService.updateReturnSuccess(returnUpdate.getOrderId());
            return refundService.completeRefund(returnUpdate);
        }
        returnService.updateReturnFailed(returnUpdate.getOrderId());
        Refund refund = refundService.getRefundsByOrderId(returnUpdate.getOrderId());
        return refundService.rejectRefund(refund.getRefundId(),"Product Damaged.");
    }

    // BUYER: Cancel the order
    @PreAuthorize("hasPermission('ORDER', 'DELETE')")
    @PostMapping("/cancelOrder/{orderId}")
    public void cancelOrder(@PathVariable String orderId){
        refundService.cancelOrder(orderId,"Ordered by mistake");
    }



}
