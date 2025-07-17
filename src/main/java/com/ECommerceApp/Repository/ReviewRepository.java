package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review,String > {
    List<Review> findByProductId(String productId);
    List<Review> findByUserId(String userId);

    @Query("{'userId':?0}")
    boolean existsByUserId(String userId);
    @Query("{'userId':?0}")
    void deleteByUserId(String userId);
}
