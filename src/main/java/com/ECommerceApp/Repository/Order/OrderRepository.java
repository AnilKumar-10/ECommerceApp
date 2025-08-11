package com.ECommerceApp.Repository;

import com.ECommerceApp.DTO.Order.OrderStatusCount;
import com.ECommerceApp.Model.Order.Order;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order,String > {
//    @Query("{}")
    List<Order> findAllByBuyerId(String userId);

    @Query("{ 'orderStatus': 'PENDING' }")
    List<Order> findAllPendingOrders();

    @Aggregation(pipeline = {
            "{ $group: { _id: '$orderStatus', total: { $sum: 1 } } }",
            "{ $sort: { total: -1 } }"
    })
    List<OrderStatusCount> countOrdersByStatus();
}
