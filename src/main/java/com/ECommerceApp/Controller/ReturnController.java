package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.RaiseRefundRequestDto;
import com.ECommerceApp.DTO.RefundAndReturnResponseDTO;
import com.ECommerceApp.DTO.ReturnUpdate;
import com.ECommerceApp.Model.DeliveryPerson;
import com.ECommerceApp.Model.Refund;
import com.ECommerceApp.Service.DeliveryService;
import com.ECommerceApp.Service.RefundService;
import com.ECommerceApp.Service.ReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReturnController {

    @Autowired
    private RefundService refundService;
    @Autowired
    private ReturnService returnService;
    @Autowired
    private DeliveryService deliveryService;


    @PostMapping("/requestRefund")
    public RefundAndReturnResponseDTO raiseRefundReq(@RequestBody RaiseRefundRequestDto refundRequestDto){
        return refundService.requestRefund(refundRequestDto);
    }


    @PostMapping("/updateReturn")
    public Refund updateReturn(@RequestBody ReturnUpdate returnUpdate){
        if(returnUpdate.isPicked()){
            returnService.updateReturnSuccess(returnUpdate.getOrderId());
            DeliveryPerson deliveryPerson = deliveryService.getDeliveryPerson(returnUpdate.getDeliveryPersonId());
            deliveryPerson.getToReturnItems();
            Refund refund = refundService.getRefundsByOrderId(returnUpdate.getOrderId());
            return refundService.completeRefund(refund.getRefundId());
        }
        returnService.updateReturnFailed(returnUpdate.getOrderId());
        Refund refund = refundService.getRefundsByOrderId(returnUpdate.getOrderId());
        return refundService.rejectRefund(refund.getRefundId(),"Product Damaged.");
    }
}
