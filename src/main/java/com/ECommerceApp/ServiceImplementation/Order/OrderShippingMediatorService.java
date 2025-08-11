package com.ECommerceApp.ServiceImplementation.Order;

import com.ECommerceApp.Exceptions.Order.OrderNotFoundException;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderShippingMediatorService {
    @Autowired
    private OrderRepository orderRepository;


    public Order saveOrder(Order order){
        return orderRepository.save(order);
    }


    public Order getOrder(String id){
        log.info("getting the order with id: {}", id);
        return orderRepository.findById(id).orElseThrow(()-> new OrderNotFoundException("There is no order found with id: "+id));
    }

}
