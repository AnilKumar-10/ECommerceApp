package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.Invoice;
import com.ECommerceApp.Service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/genInvoice/{orderid}")
    public Invoice generateInvoice(@PathVariable String orderid){
        return invoiceService.generateInvoice(orderid);
    }


    @GetMapping("/getInvoiceById/{invoiceId}")
    public Invoice getInVoice(@PathVariable String invoiceId){
        return invoiceService.getInvoiceById(invoiceId);
    }


    @GetMapping("/getInvoiceByOrder/{invoiceId}")
    public Invoice getInVoiceByOrder(@PathVariable String orderId){
        return invoiceService.getInvoiceByOrderId(orderId);
    }

    @GetMapping("/getInvoiceByUser/(userId)")
    public List<Invoice> getAllInvoicesByUser(@PathVariable String userId){
        return invoiceService.getInvoicesByUserId(userId);
    }

    @GetMapping("/getAllInvoices")
    public List<Invoice> getALlInvoices(){
        return invoiceService.getAllInvoices();
    }

}
