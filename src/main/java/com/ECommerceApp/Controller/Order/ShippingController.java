package com.ECommerceApp.Controller.Order;

import com.ECommerceApp.DTO.Order.ShippingUpdateRequest;
import com.ECommerceApp.Model.Delivery.ShippingDetails;
import com.ECommerceApp.ServiceInterface.Order.IOrderService;
import com.ECommerceApp.ServiceInterface.Order.IShippingService;
import com.ECommerceApp.Util.OwnershipGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping")
public class ShippingController { //admin,deliveryman/seller

    @Autowired
    private IShippingService shippingService;
    @Autowired
    private IOrderService orderService;

    //  ADMIN/DELIVERY: read shipping by order ID
    @PreAuthorize("hasPermission('SHIPPING', 'READ')")
    @GetMapping("/getShippingByOrder/{id}")
    public ResponseEntity<?> getShipping(@PathVariable String id) {
        ShippingDetails shippingDetails = shippingService.getShippingByOrderId(id);
        new OwnershipGuard().checkSelf(orderService.getOrder(shippingDetails.getOrderId()).getBuyerId());
        return ResponseEntity.ok(shippingDetails);
    }

    // ADMIN/DELIVERY/SELLER: update shipping status
    @PreAuthorize("hasPermission('SHIPPING', 'UPDATE')")
    @PostMapping("/updateShipping")
    public ResponseEntity<?> updateShip(@RequestBody ShippingUpdateRequest shippingUpdateDTO) {
        return ResponseEntity.ok(shippingService.updateShippingStatus(shippingUpdateDTO));
    }

    //  ADMIN/DELIVERY: delivery person accesses their assigned shipments
    @PreAuthorize("hasPermission('SHIPPING', 'READ')")
    @GetMapping("/getAllShippingByDelPerson/{personId}")
    public ResponseEntity<?> getAllShippingDetailsByDelPersonId(@PathVariable String personId) {
        return ResponseEntity.ok(shippingService.getByDeliveryPersonId(personId));
    }

    // ADMIN/DELIVERY: get shipping detail by shipping ID
    @PreAuthorize("hasPermission('SHIPPING', 'READ')")
    @GetMapping("/getShipping/{shippingId}")
    public ResponseEntity<?> getShippingById(@PathVariable String shippingId) {
        ShippingDetails shippingDetails = shippingService.getByShippingId(shippingId);
        new OwnershipGuard().checkSelf(orderService.getOrder(shippingDetails.getOrderId()).getBuyerId());
        return ResponseEntity.ok(shippingDetails);
    }
}
