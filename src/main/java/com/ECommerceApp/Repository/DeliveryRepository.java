package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface DeliveryRepository extends MongoRepository<DeliveryPerson,String> {

    @Query(
            value = "{ '_id': ?0, 'toDeliveryItems.OrderId': ?1 }",
            fields = "{ 'toDeliveryItems.$': 1, 'name': 1, 'phone': 1,'isActive':1 }"
    )
    Optional<DeliveryPerson> findSingleDeliveryItemByOrderId(String deliveryPersonId, String orderId);


    @Query(
            value = "{ 'toDeliveryItems.OrderId': ?0 }",
            fields = "{ 'toDeliveryItems.$': 1, 'name': 1, 'phone': 1,'isActive':1 }"
    )
    Optional<DeliveryPerson> findByOrderId(String orderId);


}
