package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.TaxRule;
import com.ECommerceApp.Service.TaxRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaxController {

    @Autowired
    private TaxRuleService taxRuleService;


    @PostMapping("/createTaxrules")
    public String  createTax(@RequestBody List<TaxRule> rule){
        return taxRuleService.createMultiTaxRules(rule);
    }

    @PostMapping("/createTaxRule")
    public TaxRule createTacRule(@RequestBody TaxRule rule){
        return taxRuleService.createOneTaxRule(rule);
    }

    @PutMapping("/updateTaxRule")
    public TaxRule updateTaxRule(@RequestBody TaxRule rule){
        return taxRuleService.updateTaxRule(rule);
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
