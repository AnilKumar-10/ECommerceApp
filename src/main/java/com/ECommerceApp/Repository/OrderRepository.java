package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order,String > {
}
