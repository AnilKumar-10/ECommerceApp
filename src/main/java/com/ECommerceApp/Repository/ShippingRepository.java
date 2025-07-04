package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.ShippingDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShippingRepository extends MongoRepository<ShippingDetails,String> {
}
