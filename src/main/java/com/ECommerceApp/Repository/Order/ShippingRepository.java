package com.ECommerceApp.Repository.Order;

import com.ECommerceApp.Model.Delivery.ShippingDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.*;

public interface ShippingRepository extends MongoRepository<ShippingDetails,String> {
    Optional<ShippingDetails> findByOrderId(String orderId);
    List<ShippingDetails> findByDeliveryPersonId(String deliveryPersonId);
}
