package com.ECommerceApp.Controller.Delivery;

import com.ECommerceApp.DTO.Delivery.DeliveryPersonResponse;
import com.ECommerceApp.DTO.Delivery.DeliveryUpdate;
import com.ECommerceApp.DTO.Payment.PaymentRequest;
import com.ECommerceApp.DTO.User.PasswordUpdate;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.ServiceImplementation.AuthService;
import com.ECommerceApp.ServiceImplementation.OtpService;
import com.ECommerceApp.ServiceInterface.*;
import com.ECommerceApp.ServiceInterface.IPaymentService;
import com.ECommerceApp.Util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery")
public class DeliveryController { // admin, delivery person

    @Autowired
    private IOrderService orderService;
    @Autowired
    private IPaymentService paymentService;
    @Autowired
    private IShippingService shippingService;
    @Autowired
    private IDeliveryService deliveryService;
    @Autowired
    private IExchangeService exchangeService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthService authService;

//    @PostMapping("/insertDelivery")
//    public ResponseEntity<?> insertDelivery(@Valid @RequestBody DeliveryPersonRegistrationRequest deliveryPerson ){
//        return  ResponseEntity.ok(deliveryService.register(deliveryPerson));
//    }
//
//
//    @PostMapping("/insertDeliveries")
//    public ResponseEntity<?>  insertDeliveryPersons(@Valid @RequestBody List<@Valid DeliveryPersonRegistrationRequest> deliveryPerson){
//        return  ResponseEntity.ok(deliveryService.registerPersons(deliveryPerson));
//    }

    @PreAuthorize("@permissionService.hasPermission('DELIVERY', 'READ')")
    @GetMapping("/sendOtp")
    public ResponseEntity<String> sendOtp() {
        String email = new SecurityUtils().getCurrentUserMail();
        otpService.sendOtpToEmail(email);
        return ResponseEntity.ok("OTP sent to " + email);
    }

    @PreAuthorize("@permissionService.hasPermission('DELIVERY', 'UPDATE')")
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid PasswordUpdate request) {
        boolean isValidOtp = otpService.validateOtp(request.getEmail(), request.getOtp());
        if (!isValidOtp) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        request.setNewPassword(passwordEncoder.encode(request.getNewPassword()));
        String response = authService.updateDeliveryPassword(request);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("@permissionService.hasPermission('DELIVERY', 'UPDATE')")
    @PutMapping("/updateDelivery")
    public ResponseEntity<?> updateDelivery(@Valid @RequestBody DeliveryUpdate deliveryUpdateDTO){
        if(orderService.getOrder(deliveryUpdateDTO.getOrderId()).getPaymentMethod()== Payment.PaymentMethod.COD){
            System.out.println("inside the if of update: "+deliveryUpdateDTO);
            PaymentRequest paymentDto = new PaymentRequest();
            paymentDto.setPaymentId(deliveryUpdateDTO.getPaymentId());
            paymentDto.setTransactionId(orderService.generateTransactionIdForCOD());
            paymentDto.setStatus(Payment.PaymentStatus.SUCCESS);
            paymentService.confirmCODPayment(paymentDto); // updating the payment success details
            orderService.updateCODPaymentStatus(deliveryUpdateDTO);// updating the order payment status
        }
        return ResponseEntity.ok(shippingService.updateDeliveryStatus(deliveryUpdateDTO));
    }


    @PreAuthorize("@permissionService.hasPermission('DELIVERY', 'DELETE')")
    @DeleteMapping("/deleteDeliveryPerson/{id}")
    public String deleteDeliveryPerson(@PathVariable String id){
        return deliveryService.deleteDeliveryMan(id);
    }



    @PreAuthorize("@permissionService.hasPermission('DELIVERY', 'READ')")
    @GetMapping("/getDeliveryPerson/{id}")
    public DeliveryPerson getDeliveryPerson(@PathVariable String id){
        return deliveryService.getDeliveryPerson(id);
    }



    @PreAuthorize("@permissionService.hasPermission('DELIVERY', 'READ')")
    @GetMapping("/getDelPersonByOrder/{orderId}")
    public DeliveryPersonResponse getByOrderId(@PathVariable String orderId){
        return deliveryService.getDeliveryPersonByOrderId(orderId);
    }



    @PreAuthorize("@permissionService.hasPermission('DELIVERY', 'READ')")
    @GetMapping("/getData")
    public DeliveryPerson getDeliveryPersonData(){
        return deliveryService.getDeliveryPeronData();
    }

}
