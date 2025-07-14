package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product,String> {
    List<Product> findByCategoryIdsIn(List<String> categoryIds);
    List<Product> findBySellerId(String sellerId);
    @Query("{ 'categoryIds': { $all: ?0 } }")
    List<Product> findByCategoryIdsContainingAll(List<String> categoryIds);

}
