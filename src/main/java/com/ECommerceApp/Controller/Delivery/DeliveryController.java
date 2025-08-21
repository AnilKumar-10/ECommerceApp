package com.ECommerceApp.Controller.Delivery;

import com.ECommerceApp.DTO.Delivery.DeliveryPersonResponse;
import com.ECommerceApp.DTO.Delivery.DeliveryUpdate;
import com.ECommerceApp.DTO.Payment.PaymentRequest;
import com.ECommerceApp.DTO.User.PasswordUpdate;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.ServiceImplementation.User.AuthService;
import com.ECommerceApp.ServiceImplementation.User.OtpService;
import com.ECommerceApp.ServiceInterface.Delivery.IDeliveryService;
import com.ECommerceApp.ServiceInterface.Order.IShippingService;
import com.ECommerceApp.ServiceInterface.Payment.IPaymentService;
import com.ECommerceApp.ServiceInterface.Order.IExchangeService;
import com.ECommerceApp.ServiceInterface.Order.IOrderService;
import com.ECommerceApp.Util.OwnershipGuard;
import com.ECommerceApp.Util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery")
public class  DeliveryController { // admin, delivery person

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

    //  SELF: current delivery person requesting OTP
    @PreAuthorize("hasPermission('DELIVERY', 'READ')")
    @GetMapping("/sendOtp")
    public ResponseEntity<String> sendOtp() {
        String email = new SecurityUtils().getCurrentUserMail();
        otpService.sendOtpToEmail(email);
        return ResponseEntity.ok("OTP sent to " + email);
    }

    //  SELF: delivery person resets their own password
    @PreAuthorize("hasPermission('DELIVERY', 'UPDATE')")
    @PutMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid PasswordUpdate request) {
        boolean isValidOtp = otpService.validateOtp(request.getEmail(), request.getOtp());
        if (!isValidOtp) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        request.setNewPassword(passwordEncoder.encode(request.getNewPassword()));
        String response = authService.updateDeliveryPassword(request);
        return ResponseEntity.ok(response);
    }

    // SELF: delivery person updates their delivery status
    @PreAuthorize("hasPermission('DELIVERY', 'UPDATE')")
    @PutMapping("/updateDelivery")
    public ResponseEntity<?> updateDelivery(@Valid @RequestBody DeliveryUpdate deliveryUpdateDTO){
        if (orderService.getOrder(deliveryUpdateDTO.getOrderId()).getPaymentMethod() == Payment.PaymentMethod.COD) {
            PaymentRequest paymentDto = new PaymentRequest();
            paymentDto.setPaymentId(deliveryUpdateDTO.getPaymentId());
            paymentDto.setTransactionId(orderService.generateTransactionIdForCOD());
            paymentDto.setStatus(Payment.PaymentStatus.SUCCESS);

            paymentService.confirmCODPayment(paymentDto);
            orderService.updateCODPaymentStatus(deliveryUpdateDTO);
        }
        return ResponseEntity.ok(shippingService.updateDeliveryStatus(deliveryUpdateDTO));
    }

    // ADMIN ONLY: delete any delivery person
    @PreAuthorize("hasPermission(#id, 'com.ECommerceApp.Model.DeliveryPerson', 'DELETE')")
    @DeleteMapping("/deleteDeliveryPerson/{id}")
    public ResponseEntity<?> deleteDeliveryPerson(@PathVariable String id) {
        return ResponseEntity.ok(deliveryService.deleteDeliveryMan(id));
    }

    // ADMIN or SELF: read a specific delivery person by ID
    @PreAuthorize("hasPermission(#id, 'com.ECommerceApp.Model.DeliveryPerson', 'READ')")
        @GetMapping("/getDeliveryPerson/{id}")
    public ResponseEntity<?> getDeliveryPerson(@PathVariable String id) {
        return ResponseEntity.ok(deliveryService.getDeliveryPerson(id));
    }

    // ADMIN ONLY: get delivery person by order
    @PreAuthorize("hasPermission('DELIVERY', 'READ')")
    @GetMapping("/getDelPersonByOrder/{orderId}")
    public ResponseEntity<?> getByOrderId(@PathVariable String orderId) {
        new OwnershipGuard().checkAdmin();
        return ResponseEntity.ok(deliveryService.getDeliveryPersonByOrderId(orderId));
    }

    // SELF ONLY: logged-in delivery person reads their own profile
    @PreAuthorize("hasPermission('DELIVERY', 'READ')")
    @GetMapping("/getData")
    public ResponseEntity<?> getDeliveryPersonData() {
        return ResponseEntity.ok(deliveryService.getDeliveryPersonData()); // current user ID from JWT internally
    }

    @PutMapping("/update")//update the delivery person.
    public ResponseEntity<?> updateDelivery(@RequestBody DeliveryPerson deliveryPerson){
        deliveryPerson.setPassword(passwordEncoder.encode(deliveryPerson.getPassword()));
        return ResponseEntity.ok(deliveryService.updateDelivery(deliveryPerson));
    }
}
