package com.ECommerceApp.Repository.Product;

import com.ECommerceApp.Model.Product.StockLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StockLogRepository extends MongoRepository<StockLog,String > {
    Optional<StockLog> findByProductIdAndSellerId(String productId, String sellerId);

    StockLog findByProductId(String productId);
}
