package com.ECommerceApp.Controller.Order;

import com.ECommerceApp.DTO.Order.PlaceOrderRequest;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.ServiceInterface.Order.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController { //user from service classes

    @Autowired
    private IOrderService orderService;


    //  BUYER places an order → INSERT permission
    @PreAuthorize("hasPermission('ORDER', 'INSERT')")
    @PostMapping("/placeOrder")
    public Order placeOrder(@RequestBody PlaceOrderRequest orderDto) {
        return orderService.createOrder(orderDto);
    }

    //  ADMIN/SELLER(SELF) reads any order → scope: ALL
    @PreAuthorize("hasPermission('ORDER', 'READ')")
    @GetMapping("/getOrder/{id}")
    public Order getOrder(@PathVariable String id) {
        return orderService.getOrder(id);
    }

    //  BUYER (SELF) reads their own orders → scope: SELF
    //  ADMIN reads any user's orders → scope: ALL
    @PreAuthorize("hasPermission(#userId, 'com.ECommerceApp.Model.User', 'READ')")
    @GetMapping("/getAllOrderByUser/{userId}")
    public List<Order> getAllOrdersByUser(@PathVariable String userId) {
        return orderService.getAllOrderByUserId(userId);
    }

    //  ADMIN only
    @PreAuthorize("hasPermission('ORDER', 'READ')")
    @GetMapping("/getAllOrders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    //  ADMIN only
    @PreAuthorize("hasPermission('ORDER', 'READ')")
    @GetMapping("/getPendingOrders")
    public List<Order> getAllPendingOrders() {
        return orderService.getAllPendingOrders();
    }
}
