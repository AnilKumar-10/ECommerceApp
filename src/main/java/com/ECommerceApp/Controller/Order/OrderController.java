package com.ECommerceApp.Controller.Order;

import com.ECommerceApp.DTO.Order.PlaceOrderRequest;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.ServiceInterface.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController { //user from service classes

    @Autowired
    private IOrderService orderService;

    @PostMapping("/placeOrder")
    public Order placeOrder(@RequestBody PlaceOrderRequest orderDto){
        return orderService.createOrder(orderDto);
    }

    @GetMapping("/getOrder/{id}")
    public Order getOrder(@PathVariable String id){
        return orderService.getOrder(id);
    }

    @GetMapping("/getAllOrderByUser/{userId}")
    public List<Order> getAllOrdersByUser(@PathVariable String userId){
        return orderService.getAllOrderByUserId(userId);
    }

    @GetMapping("/getAllOrders")
    public List<Order> getAllOrders(){
        return orderService.getAllOrders();
    }

    @GetMapping("/getPendingOrders")
    public List<Order> getAllPendingOrders(){
        return orderService.getAllPendingOrders();
    }

}
