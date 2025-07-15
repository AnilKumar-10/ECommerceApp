package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.ShippingUpdateDTO;
import com.ECommerceApp.Model.ShippingDetails;
import com.ECommerceApp.Service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShippingController {

    @Autowired
    private ShippingService shippingService;


    @GetMapping("/getShipping/{id}")
    public ShippingDetails getShipping(@PathVariable String id){
        return shippingService.getShippingByOrderId(id);
    }

    @PostMapping("/updateShipping")
    public ShippingDetails updateShip(@RequestBody ShippingUpdateDTO shippingUpdateDTO){
        return shippingService.updateShippingStatus(shippingUpdateDTO);
    }
}
