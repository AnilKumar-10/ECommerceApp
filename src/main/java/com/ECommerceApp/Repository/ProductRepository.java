package com.ECommerceApp.Repository;

import com.ECommerceApp.DTO.ProductSearchResponseDto;
import com.ECommerceApp.Model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


import java.util.List;

public interface ProductRepository extends MongoRepository<Product,String> {
    List<Product> findByCategoryIdsIn(List<String> categoryIds);
    List<Product> findBySellerId(String sellerId);
    @Query("{ 'categoryIds': { $all: ?0 } }")
    List<Product> findByCategoryIdsContainingAll(List<String> categoryIds);

    @Query("{ 'brand': { $regex: ?0, $options: 'i' } }")
    List<ProductSearchResponseDto> findByBrandIgnoreCase(String brandName);

    @Query("{ 'categoryIds': { $all: ?0 }, 'brand': { $regex: ?1, $options: 'i' } }")
    List<Product> findByCategoryIdsAndBrandIgnoreCase(List<String> categoryIds, String brand);

    @Query("{ 'categoryIds': { $all: ?0 }, 'brand': { $regex: ?1, $options: 'i' } }")
    List<Product> findByCategoryIdsContainingAllAndBrandIgnoreCase(List<String> categoryIds, String brand);

    Page<Product> findAll(Pageable pageable);
}
