package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.DeliveryPersonResponseDto;
import com.ECommerceApp.DTO.DeliveryUpdateDTO;
import com.ECommerceApp.DTO.PaymentDto;
import com.ECommerceApp.Model.DeliveryPerson;
import com.ECommerceApp.Service.DeliveryService;
import com.ECommerceApp.Service.OrderService;
import com.ECommerceApp.Service.PaymentService;
import com.ECommerceApp.Service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeliveryController { // admin, delivery person

    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ShippingService shippingService;
    @Autowired
    private DeliveryService deliveryService;


    @PostMapping("/insertDelivery")
    public DeliveryPerson insertDelivery(@RequestBody DeliveryPerson deliveryPerson){
        return  deliveryService.register(deliveryPerson);
    }

    @PostMapping("/insertDeliveries")
    public String  insertDeliveryPersons(@RequestBody List<DeliveryPerson> deliveryPerson){
        return  deliveryService.registerPersons(deliveryPerson);
    }


    @PostMapping("/updateDelivery")
    public String updateDelivery(@RequestBody DeliveryUpdateDTO deliveryUpdateDTO){
        if(orderService.getOrder(deliveryUpdateDTO.getOrderId()).getPaymentMethod().equalsIgnoreCase("COD")){
            System.out.println("inside the if of update: "+deliveryUpdateDTO);
            PaymentDto paymentDto = new PaymentDto();
            paymentDto.setPaymentId(deliveryUpdateDTO.getPaymentId());
            paymentDto.setTransactionId(orderService.generateTransactionIdForCOD());
            paymentDto.setStatus("SUCCESS");
            paymentService.confirmCODPayment(paymentDto); // updating the payment success details
            orderService.updateCODPaymentStatus(deliveryUpdateDTO);// updating the order payment status
        }
        return shippingService.updateDeliveryStatus(deliveryUpdateDTO);
    }

    @DeleteMapping("/deleteDeliveryPerson/{id}")
    public String deleteDeliveryPerson(@PathVariable String id){
        return deliveryService.deleteDeliveryMan(id);
    }

    @GetMapping("/getDeliveryPerson/{id}")
    public DeliveryPerson getDeliveryPerson(@PathVariable String id){
        return deliveryService.getDeliveryPerson(id);
    }

    @GetMapping("/getDelPersonByOrder/{orderId}")
    public DeliveryPersonResponseDto getByOrderId(@PathVariable String orderId){
        return deliveryService.getDeliveryPersonByOrderId(orderId);
    }

}
