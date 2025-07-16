package com.ECommerceApp.Service;

import com.ECommerceApp.Exceptions.TaxRuleNotFoundException;
import com.ECommerceApp.Model.TaxRule;
import com.ECommerceApp.Repository.TaxRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaxRuleService {
    @Autowired
    private TaxRuleRepository taxRuleRepository;

    // 1. Create new tax rule
    public TaxRule createOneTaxRule(TaxRule rule) {
        return taxRuleRepository.save(rule);
    }

    public String  createMultiTaxRules(List<TaxRule> taxRules) {
        int count = 0;

        for(TaxRule rule : taxRules){
            taxRuleRepository.save(rule);
            count++;
        }
        if(count==taxRules.size()){
             return "taxRules are inserted successfully!  "+count;
        }
        return "Something went wrong";
    }

    // 2. Update existing tax rule
    public TaxRule updateTaxRule(TaxRule updated) {
        TaxRule existing = taxRuleRepository.findById(updated.getId())
                .orElseThrow(() -> new TaxRuleNotFoundException("TaxRule not found"));

        existing.setCountry(updated.getCountry());
        existing.setState(updated.getState());
        existing.setCategoryId(updated.getCategoryId());
        existing.setTaxRate(updated.getTaxRate());
        existing.setActive(updated.isActive());

        return taxRuleRepository.save(existing);
    }

    // 3. Delete tax rule
    public String deleteTaxRule(String id) {
        if (!taxRuleRepository.existsById(id)) {
            throw new TaxRuleNotFoundException("TaxRule not found");
        }
        taxRuleRepository.deleteById(id);
        return "Deleted Successfully";
    }

    // 4. Get all tax rules (admin)
    public List<TaxRule> getAllTaxRules() {
        return taxRuleRepository.findAll();
    }

    // 5. Get applicable tax rate based on state and category
    public double getApplicableTaxRate(String categoryId , String state) {
        TaxRule rule = taxRuleRepository.findByStateAndCategoryIdAndIsActiveTrue(state, categoryId)
                .orElse(null);
        if (rule == null) {
            return 0.0;
        }
        return rule.getTaxRate();
    }

    // 6. Get tax rule by category and state
    public Optional<TaxRule> getTaxRule(String categoryId, String state ) {
        return taxRuleRepository.findByStateAndCategoryIdAndIsActiveTrue(state, categoryId);
    }
}
