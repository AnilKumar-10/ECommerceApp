package com.ECommerceApp.Controller.Order;

import com.ECommerceApp.DTO.Order.PlaceOrderRequest;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.ServiceInterface.Order.IOrderService;
import com.ECommerceApp.Util.OwnershipGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> placeOrder(@RequestBody PlaceOrderRequest orderDto) {
        return ResponseEntity.ok(orderService.createOrder(orderDto));
    }

    //  ADMIN/SELLER reads any order → scope: ALL
    @PreAuthorize("hasPermission('ORDER', 'READ')")
    @GetMapping("/getOrder/{id}")
    public ResponseEntity<?> getOrder(@PathVariable String id) {
        Order order = orderService.getOrder(id);
        new OwnershipGuard().checkSelf(order.getBuyerId()); // this checks the ownership
        return ResponseEntity.ok(order);
    }

    //  BUYER (SELF) reads their own orders → scope: SELF
    //  ADMIN reads any user's orders → scope: ALL
    @PreAuthorize("hasPermission(#userId, 'com.ECommerceApp.Model.User', 'READ')")
    @GetMapping("/getAllOrderByUser/{userId}")
    public ResponseEntity<?> getAllOrdersByUser(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getAllOrderByUserId(userId));
    }

    //  ADMIN only
    @PreAuthorize("hasPermission('ORDER', 'READ')")
    @GetMapping("/getAllOrders")
    public ResponseEntity<?> getAllOrders() {
        new OwnershipGuard().checkAdmin();
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    //  ADMIN only
    @PreAuthorize("hasPermission('ORDER', 'READ')")
    @GetMapping("/getPendingOrders")
    public ResponseEntity<?> getAllPendingOrders() {
        new OwnershipGuard().checkAdmin();
        return ResponseEntity.ok(orderService.getAllPendingOrders());
    }
}
