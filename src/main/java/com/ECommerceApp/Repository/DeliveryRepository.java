package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.DeliveryPerson;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeliveryRepository extends MongoRepository<DeliveryPerson,String> {
}
