package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.TaxRule;
import com.ECommerceApp.Service.TaxRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaxController {

    @Autowired
    private TaxRuleService taxRuleService;


    @PostMapping("/createTax")
    public String  createTax(@RequestBody List<TaxRule> rule){
        return taxRuleService.createMultiTaxRules(rule);
    }

}
