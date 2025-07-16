package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.ShippingUpdateDTO;
import com.ECommerceApp.Model.ShippingDetails;
import com.ECommerceApp.Service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShippingController {

    @Autowired
    private ShippingService shippingService;


    @GetMapping("/getShippingByOrder/{id}")
    public ShippingDetails getShipping(@PathVariable String id){
        return shippingService.getShippingByOrderId(id);
    }

    @PostMapping("/updateShipping")
    public ShippingDetails updateShip(@RequestBody ShippingUpdateDTO shippingUpdateDTO){
        return shippingService.updateShippingStatus(shippingUpdateDTO);
    }

    @GetMapping("/getAllShippingByDelPerson/{personId}")
    public List<ShippingDetails> getAllShippingDetailsByDelPersonId(@PathVariable String personId){
        return shippingService.getByDeliveryPersonId(personId);
    }

    @GetMapping("/getShipping/{shippingId}")
    public ShippingDetails getShippingById(@PathVariable String shippingId){
        return shippingService.getByShippingId(shippingId);
    }
}
