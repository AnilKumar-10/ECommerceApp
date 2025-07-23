package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Order.TaxRule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TaxRuleRepository extends MongoRepository<TaxRule, String> {

    Optional<TaxRule> findByStateAndCategoryIdAndIsActiveTrue(String state, String categoryId);

    // Optional fallback if needed
//    List<TaxRule> findByCountryAndCategoryIdAndIsActiveTrue(String country, String categoryId);

    TaxRule findByCategoryIdAndStateAndIsActiveTrue(String rootCategoryId,String  shippingState);
}
