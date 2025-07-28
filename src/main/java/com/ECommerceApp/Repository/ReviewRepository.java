package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Product.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends MongoRepository<Review,String > {
    List<Review> findByProductId(String productId);
    List<Review> findByUserId(String userId);
    Optional<Review> findByProductIdAndUserId(String productId, String userId);

    @Query("{'userId':?0}")
    boolean existsByUserId(String userId);

    void deleteByUserIdAndProductId(String userId, String productId);

}
