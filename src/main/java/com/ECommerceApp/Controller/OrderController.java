package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.PlaceOrderDto;
import com.ECommerceApp.Model.Order;
import com.ECommerceApp.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.datatransfer.Clipboard;
import java.util.List;

@RestController
public class OrderController { //user

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



//    public void cancelOrder(@PathVariable String orderId){
//        orderService.
//    }
}
