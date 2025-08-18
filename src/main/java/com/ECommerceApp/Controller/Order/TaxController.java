package com.ECommerceApp.Controller.Order;

import com.ECommerceApp.DTO.Product.TaxRuleCreationRequest;
import com.ECommerceApp.Model.Order.TaxRule;
import com.ECommerceApp.ServiceInterface.Order.ITaxRuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tax")
public class TaxController { //admin

    @Autowired
    private ITaxRuleService taxRuleService;

    // ADMIN only
    @PreAuthorize("hasPermission('TAX', 'INSERT')")
    @PostMapping("/createTaxRules")
    public ResponseEntity<?> createTax(@Valid @RequestBody List<TaxRuleCreationRequest> rule) {
        return ResponseEntity.ok(taxRuleService.createMultiTaxRules(rule));
    }

    //ADMIN only
    @PreAuthorize("hasPermission('TAX', 'INSERT')")
    @PostMapping("/createTaxRule")
    public ResponseEntity<?> createTaxRule(@RequestBody TaxRuleCreationRequest rule) {
        return ResponseEntity.ok(taxRuleService.createOneTaxRule(rule));
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
    public ResponseEntity<?> deleteTaxRule(@PathVariable String ruleId) {
        return ResponseEntity.ok(taxRuleService.deleteTaxRule(ruleId));
    }

    // All roles can READ
    @PreAuthorize("hasPermission('TAX', 'READ')")
    @GetMapping("/getAllTaxRules")
    public ResponseEntity<?> getAllTaxRules() {
        return ResponseEntity.ok(taxRuleService.getAllTaxRules());
    }

    @PreAuthorize("hasPermission('TAX', 'READ')")
    @GetMapping("/getTaxRulesByState/{state}")
    public ResponseEntity<?> getTaxByState(@PathVariable String state){
        return ResponseEntity.ok(taxRuleService.getAllTaxRulesByState(state));
    }
}
