package com.ECommerceApp.Controller.Order;

import com.ECommerceApp.DTO.Product.TaxRuleCreationRequest;
import com.ECommerceApp.Model.Order.TaxRule;
import com.ECommerceApp.Service.TaxRuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TaxController { //admin

    @Autowired
    private TaxRuleService taxRuleService;


    @PostMapping("/createTaxrules")
    public ResponseEntity<?>  createTax(@Valid @RequestBody List<TaxRuleCreationRequest> rule, BindingResult result){
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok(taxRuleService.createMultiTaxRules(rule));
    }

    @PostMapping("/createTaxRule")
    public TaxRule createTacRule(@RequestBody TaxRuleCreationRequest rule){
        return taxRuleService.createOneTaxRule(rule);
    }

    @PutMapping("/updateTaxRule")
    public ResponseEntity<?> updateTaxRule(@Valid @RequestBody TaxRuleCreationRequest rule){
        return ResponseEntity.ok(taxRuleService.updateTaxRule(rule));
    }

    @DeleteMapping("/deleteTaxRule/{ruleId}")
    public String deleteTaxRule(@PathVariable String ruleId){
        return taxRuleService.deleteTaxRule(ruleId);
    }

    @GetMapping("/getAllTaxRules")
    public List<TaxRule> getAllTaxRules(){
        return taxRuleService.getAllTaxRules();
    }

}
