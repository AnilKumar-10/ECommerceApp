package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WishListRepository extends MongoRepository<Wishlist,String > {
    Optional<Wishlist> findByBuyerId(String buyerId);
}
