package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.DeliveryPersonRegistrationDto;
import com.ECommerceApp.DTO.DeliveryPersonResponseDto;
import com.ECommerceApp.DTO.DeliveryUpdateDTO;
import com.ECommerceApp.DTO.PaymentDto;
import com.ECommerceApp.Model.DeliveryPerson;
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
    public ResponseEntity<?> insertDelivery(@Valid @RequestBody DeliveryPersonRegistrationDto deliveryPerson ){
        return  ResponseEntity.ok(deliveryService.register(deliveryPerson));
    }

    @PostMapping("/insertDeliveries")
    public ResponseEntity<?>  insertDeliveryPersons(@Valid @RequestBody List<@Valid  DeliveryPersonRegistrationDto> deliveryPerson){
        return  ResponseEntity.ok(deliveryService.registerPersons(deliveryPerson));
    }


    @PostMapping("/updateDelivery")
    public ResponseEntity<?> updateDelivery(@Valid @RequestBody DeliveryUpdateDTO deliveryUpdateDTO){
        if(orderService.getOrder(deliveryUpdateDTO.getOrderId()).getPaymentMethod().equalsIgnoreCase("COD")){
            System.out.println("inside the if of update: "+deliveryUpdateDTO);
            PaymentDto paymentDto = new PaymentDto();
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
    public DeliveryPersonResponseDto getByOrderId(@PathVariable String orderId){
        return deliveryService.getDeliveryPersonByOrderId(orderId);
    }

}
