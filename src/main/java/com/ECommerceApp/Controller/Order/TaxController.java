package com.ECommerceApp.Controller.Order;

import com.ECommerceApp.DTO.Product.TaxRuleCreationRequest;
import com.ECommerceApp.Model.Order.TaxRule;
import com.ECommerceApp.ServiceInterface.Order.ITaxRuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tax")
public class TaxController { //admin

    @Autowired
    private ITaxRuleService taxRuleService;

    // ADMIN only
    @PreAuthorize("hasPermission('TAX', 'INSERT')")
    @PostMapping("/createTaxrules")
    public ResponseEntity<?> createTax(@Valid @RequestBody List<TaxRuleCreationRequest> rule) {
        return ResponseEntity.ok(taxRuleService.createMultiTaxRules(rule));
    }

    //ADMIN only
    @PreAuthorize("hasPermission('TAX', 'INSERT')")
    @PostMapping("/createTaxRule")
    public TaxRule createTaxRule(@RequestBody TaxRuleCreationRequest rule) {
        return taxRuleService.createOneTaxRule(rule);
    }

    // ADMIN only
    @PreAuthorize("hasPermission('TAX', 'UPDATE')")
    @PutMapping("/updateTaxRule")
    public ResponseEntity<?> updateTaxRule(@Valid @RequestBody TaxRuleCreationRequest rule) {
        return ResponseEntity.ok(taxRuleService.updateTaxRule(rule));
    }

    // ADMIN only
    @PreAuthorize("hasPermission('TAX', 'DELETE')")
    @DeleteMapping("/deleteTaxRule/{ruleId}")
    public String deleteTaxRule(@PathVariable String ruleId) {
        return taxRuleService.deleteTaxRule(ruleId);
    }

    // All roles can READ
    @PreAuthorize("hasPermission('TAX', 'READ')")
    @GetMapping("/getAllTaxRules")
    public List<TaxRule> getAllTaxRules() {
        return taxRuleService.getAllTaxRules();
    }

}
