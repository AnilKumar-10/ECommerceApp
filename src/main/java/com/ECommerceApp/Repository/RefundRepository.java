package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.RefundAndExchange.Refund;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RefundRepository extends MongoRepository<Refund,String >{
    List<Refund> findByUserId(String userId);
    List<Refund> findByStatus(String status);
    Refund findByOrderId(String orderId);

}
