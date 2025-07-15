package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.PlaceOrderDto;
import com.ECommerceApp.Model.Order;
import com.ECommerceApp.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {


    @Autowired
    private OrderService orderService;


    @PostMapping("/placeOrder")
    public Order placeOrder(@RequestBody PlaceOrderDto orderDto){
        return orderService.createOrder(orderDto);
    }

    @GetMapping("/getOrder/{id}")
    public Order getOrder(@PathVariable String id){
        return orderService.getOrder(id);
    }
}
