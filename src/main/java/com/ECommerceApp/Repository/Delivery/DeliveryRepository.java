package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
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

    boolean existsByEmail(@Email(message = "Invalid email") @NotBlank(message = "Email is required") String email);

    Optional<DeliveryPerson> findByEmail(String email);

    List<DeliveryPerson> findByIsActiveTrue();
}
