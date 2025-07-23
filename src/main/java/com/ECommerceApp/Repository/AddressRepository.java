package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.User.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AddressRepository extends MongoRepository<Address,String> {
    @Query("{userId:?0}")
    List<Address> findByUserId(String userId);
}
