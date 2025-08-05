package com.ECommerceApp.Repository.User;

import com.ECommerceApp.Model.User.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WishListRepository extends MongoRepository<Wishlist,String > {
    Optional<Wishlist> findByBuyerId(String buyerId);

}
