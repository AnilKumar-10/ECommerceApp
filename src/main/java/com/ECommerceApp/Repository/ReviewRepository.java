package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review,String > {
    List<Review> findByProductId(String productId);
    List<Review> findByUserId(String userId);
}
