package com.ECommerceApp.Controller.ReturnAndExchange;

import com.ECommerceApp.DTO.Order.CancelOrderRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.*;
import com.ECommerceApp.Model.RefundAndExchange.Refund;
import com.ECommerceApp.ServiceInterface.Delivery.IDeliveryService;
import com.ECommerceApp.ServiceInterface.Order.IRefundService;
import com.ECommerceApp.ServiceInterface.Order.IReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> raiseRefundReq(@RequestBody RaiseRefundRequest refundRequestDto){
        return ResponseEntity.ok(refundService.requestRefund(refundRequestDto));
    }

    //  DELIVERY: Update return status
    @PreAuthorize("hasPermission('RETURN', 'UPDATE')")
    @PostMapping("/updateReturn")
    public ResponseEntity<?> updateReturn(@RequestBody ReturnUpdateRequest returnUpdate){
        if(returnUpdate.getPicked()){
            returnService.updateReturnSuccess(returnUpdate.getOrderId());
            return ResponseEntity.ok(refundService.completeRefund(returnUpdate));
        }
        returnService.updateReturnFailed(returnUpdate.getOrderId());
        Refund refund = refundService.getRefundsByOrderId(returnUpdate.getOrderId());
        return ResponseEntity.ok(refundService.rejectRefund(returnUpdate));
    }

    // BUYER: Cancel the order
    @PreAuthorize("hasPermission('ORDER', 'INSERT')")
    @PutMapping("/cancelOrder")
    public ResponseEntity<?> cancelOrder(@RequestBody CancelOrderRequest cancelOrderRequest){
        refundService.cancelOrder(cancelOrderRequest.getOrderId(), cancelOrderRequest.getReason());
        return ResponseEntity.ok("Order is cancelled.");
    }

    @PreAuthorize("hasPermission('RETURN', 'READ')")
    @GetMapping("/getRefundDetails/{orderId}")
    public ResponseEntity<?> getRefundDetails(@PathVariable String orderId){
        return ResponseEntity.ok(refundService.getRefundsByOrderId(orderId));
    }

}
