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
    public TaxRule createTaxRule(TaxRule rule) {
        return taxRuleRepository.save(rule);
    }

    // 2. Update existing tax rule
    public TaxRule updateTaxRule(String id, TaxRule updated) {
        TaxRule existing = taxRuleRepository.findById(id)
                .orElseThrow(() -> new TaxRuleNotFoundException("TaxRule not found"));

        existing.setCountry(updated.getCountry());
        existing.setState(updated.getState());
        existing.setCategoryId(updated.getCategoryId());
        existing.setTaxRate(updated.getTaxRate());
        existing.setActive(updated.isActive());

        return taxRuleRepository.save(existing);
    }

    // 3. Delete tax rule
    public void deleteTaxRule(String id) {
        if (!taxRuleRepository.existsById(id)) {
            throw new TaxRuleNotFoundException("TaxRule not found");
        }
        taxRuleRepository.deleteById(id);
    }

    // 4. Get all tax rules (admin)
    public List<TaxRule> getAllTaxRules() {
        return taxRuleRepository.findAll();
    }

    // 5. Get applicable tax rate based on state and category
    public double getApplicableTaxRate(String state, String categoryId) {
        TaxRule rule = taxRuleRepository.findByStateAndCategoryIdAndIsActiveTrue(state, categoryId)
                .orElse(null);

        if (rule == null) {
            // Optionally fall back to country-level default tax or 0
            return 0.0;
        }
        return rule.getTaxRate();
    }

    // 6. Optional: Get tax rule (for auditing/debuggin g)
    public Optional<TaxRule> getTaxRule(String state, String categoryId) {
        return taxRuleRepository.findByStateAndCategoryIdAndIsActiveTrue(state, categoryId);
    }
}
