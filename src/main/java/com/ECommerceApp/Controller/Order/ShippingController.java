package com.ECommerceApp.Controller.Order;

import com.ECommerceApp.DTO.Order.ShippingUpdateRequest;
import com.ECommerceApp.Model.Delivery.ShippingDetails;
import com.ECommerceApp.ServiceInterface.Order.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping")
public class ShippingController { //admin,deliveryman/seller

    @Autowired
    private IShippingService shippingService;

    //  ADMIN/DELIVERY: read shipping by order ID
    @PreAuthorize("hasPermission('SHIPPING', 'READ')")
    @GetMapping("/getShippingByOrder/{id}")
    public ShippingDetails getShipping(@PathVariable String id) {
        return shippingService.getShippingByOrderId(id);
    }

    // ADMIN/DELIVERY/SELLER: update shipping status
    @PreAuthorize("hasPermission('SHIPPING', 'UPDATE')")
    @PostMapping("/updateShipping")
    public ShippingDetails updateShip(@RequestBody ShippingUpdateRequest shippingUpdateDTO) {
        return shippingService.updateShippingStatus(shippingUpdateDTO);
    }

    //  ADMIN/DELIVERY: delivery person accesses their assigned shipments
    @PreAuthorize("hasPermission('SHIPPING', 'READ')")
    @GetMapping("/getAllShippingByDelPerson/{personId}")
    public List<ShippingDetails> getAllShippingDetailsByDelPersonId(@PathVariable String personId) {
        return shippingService.getByDeliveryPersonId(personId);
    }

    // ADMIN/DELIVERY: get shipping detail by shipping ID
    @PreAuthorize("hasPermission('SHIPPING', 'READ')")
    @GetMapping("/getShipping/{shippingId}")
    public ShippingDetails getShippingById(@PathVariable String shippingId) {
        return shippingService.getByShippingId(shippingId);
    }
}
