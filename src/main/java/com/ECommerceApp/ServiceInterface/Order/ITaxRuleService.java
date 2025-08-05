package com.ECommerceApp.ServiceInterface.Order;

import com.ECommerceApp.DTO.Product.TaxRuleCreationRequest;
import com.ECommerceApp.Model.Order.TaxRule;

import java.util.List;
import java.util.Optional;

public interface ITaxRuleService {

    TaxRule createOneTaxRule(TaxRuleCreationRequest rule);

    String createMultiTaxRules(List<TaxRuleCreationRequest> taxRules);

    TaxRule updateTaxRule(TaxRuleCreationRequest updated);

    String deleteTaxRule(String id);

    List<TaxRule> getAllTaxRules();

    double getApplicableTaxRate(String categoryId, String state);

    Optional<TaxRule> getTaxRule(String categoryId, String state);
}

