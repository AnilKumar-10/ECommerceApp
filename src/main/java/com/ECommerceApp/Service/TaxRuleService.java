package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.TaxRuleCreationRequest;
import com.ECommerceApp.Exceptions.TaxRuleNotFoundException;
import com.ECommerceApp.Model.TaxRule;
import com.ECommerceApp.Repository.TaxRuleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaxRuleService {
    @Autowired
    private TaxRuleRepository taxRuleRepository;

    // 1. Create new tax rule
    public TaxRule createOneTaxRule(TaxRuleCreationRequest rule) {
        TaxRule taxRule = new TaxRule();
        BeanUtils.copyProperties(rule,taxRule);
        return taxRuleRepository.save(taxRule);
    }

    public String  createMultiTaxRules(List<TaxRuleCreationRequest> taxRules) {
        int count = 0;
        for(TaxRuleCreationRequest rule : taxRules){
            taxRuleRepository.save(createOneTaxRule(rule));
            count++;
        }
        if(count==taxRules.size()){
             return "taxRules are inserted successfully!  "+count;
        }
        return "Something went wrong";
    }

    // 2. Update existing tax rule
    public TaxRule updateTaxRule(TaxRuleCreationRequest updated) {
        TaxRule existing = taxRuleRepository.findById(updated.getId())
                .orElseThrow(() -> new TaxRuleNotFoundException("TaxRule not found"));

        BeanUtils.copyProperties(updated,existing);
//        existing.setCountry(updated.getCountry());
//        existing.setState(updated.getState());
//        existing.setCategoryId(updated.getCategoryId());
//        existing.setTaxRate(updated.getTaxRate());
//        existing.setActive(updated.isActive());

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
