package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartRerepository extends MongoRepository<Cart,String> {
    Optional<Cart> findByBuyerId(String buyerId);
}
