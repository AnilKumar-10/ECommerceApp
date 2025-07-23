package com.ECommerceApp.Controller.Delivery;

import com.ECommerceApp.DTO.Delivery.DeliveryPersonRegistrationRequest;
import com.ECommerceApp.DTO.Delivery.DeliveryPersonResponse;
import com.ECommerceApp.DTO.Delivery.DeliveryUpdate;
import com.ECommerceApp.DTO.Payment.PaymentRequest;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Service.DeliveryService;
import com.ECommerceApp.Service.OrderService;
import com.ECommerceApp.Service.PaymentService;
import com.ECommerceApp.Service.ShippingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> insertDelivery(@Valid @RequestBody DeliveryPersonRegistrationRequest deliveryPerson ){
        return  ResponseEntity.ok(deliveryService.register(deliveryPerson));
    }

    @PostMapping("/insertDeliveries")
    public ResponseEntity<?>  insertDeliveryPersons(@Valid @RequestBody List<@Valid DeliveryPersonRegistrationRequest> deliveryPerson){
        return  ResponseEntity.ok(deliveryService.registerPersons(deliveryPerson));
    }


    @PostMapping("/updateDelivery")
    public ResponseEntity<?> updateDelivery(@Valid @RequestBody DeliveryUpdate deliveryUpdateDTO){
        if(orderService.getOrder(deliveryUpdateDTO.getOrderId()).getPaymentMethod().equalsIgnoreCase("COD")){
            System.out.println("inside the if of update: "+deliveryUpdateDTO);
            PaymentRequest paymentDto = new PaymentRequest();
            paymentDto.setPaymentId(deliveryUpdateDTO.getPaymentId());
            paymentDto.setTransactionId(orderService.generateTransactionIdForCOD());
            paymentDto.setStatus("SUCCESS");
            paymentService.confirmCODPayment(paymentDto); // updating the payment success details
            orderService.updateCODPaymentStatus(deliveryUpdateDTO);// updating the order payment status
        }
        return ResponseEntity.ok(shippingService.updateDeliveryStatus(deliveryUpdateDTO));
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
    public DeliveryPersonResponse getByOrderId(@PathVariable String orderId){
        return deliveryService.getDeliveryPersonByOrderId(orderId);
    }

}
