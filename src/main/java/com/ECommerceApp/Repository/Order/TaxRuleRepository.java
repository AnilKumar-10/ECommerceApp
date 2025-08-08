package com.ECommerceApp.Repository.Order;

import com.ECommerceApp.Model.Order.TaxRule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TaxRuleRepository extends MongoRepository<TaxRule, String> {

    Optional<TaxRule> findByStateAndCategoryIdAndIsActiveTrue(String state, String categoryId);

    // Optional fallback if needed
//    List<TaxRule> findByCountryAndCategoryIdAndIsActiveTrue(String country, String categoryId);

    TaxRule findByCategoryIdAndStateAndIsActiveTrue(String rootCategoryId,String  shippingState);

    TaxRule findByState(String state);

    List<TaxRule> findAllByState(String state);
}
