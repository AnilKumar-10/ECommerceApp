package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.Invoice;
import com.ECommerceApp.Service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidAlgorithmParameterException;

@RestController
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/genInvoice/{orderid}")
    public Invoice generateInvoice(@PathVariable String orderid){
        return invoiceService.generateInvoice(orderid);
    }

}
