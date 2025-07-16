package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order,String > {
//    @Query("{}")
    List<Order> findAllByBuyerId(String userId);

    @Query("{ 'orderStatus': 'PENDING' }")
    List<Order> findAllPendingOrders();

}
