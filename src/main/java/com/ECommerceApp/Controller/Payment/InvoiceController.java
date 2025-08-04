package com.ECommerceApp.Controller.Payment;


import com.ECommerceApp.Model.Payment.Invoice;
import com.ECommerceApp.ServiceImplementation.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/invoice")
public class InvoiceController { // admin, user

    @Autowired
    private InvoiceService invoiceService;

    //  BUYER (self) — reads own invoice by orderId
    @PreAuthorize("hasPermission('INVOICE', 'INSERT')")
    @GetMapping("/genInvoice/{orderid}")
    public Invoice generateInvoice(@PathVariable String orderid) {
        return invoiceService.generateInvoice(orderid);
    }

    //  ADMIN — reads invoice by ID
    @PreAuthorize("hasPermission('INVOICE', 'READ')")
    @GetMapping("/getInvoiceById/{invoiceId}")
    public Invoice getInVoice(@PathVariable String invoiceId) {
        return invoiceService.getInvoiceById(invoiceId);
    }

    //  ADMIN — reads invoice by order
    @PreAuthorize("hasPermission('INVOICE', 'READ')")
    @GetMapping("/getInvoiceByOrder/{orderId}")
    public Invoice getInVoiceByOrder(@PathVariable String orderId) {
        return invoiceService.getInvoiceByOrderId(orderId);
    }

    //  BUYER (self) or ADMIN — gets all invoices for specific user
    @PreAuthorize("hasPermission(#userId, 'com.ECommerceApp.Model.User', 'READ')")
    @GetMapping("/getInvoiceByUser/{userId}")
    public List<Invoice> getAllInvoicesByUser(@PathVariable String userId) {
        return invoiceService.getInvoicesByUserId(userId);
    }

    //  ADMIN — gets all invoices
    @PreAuthorize("hasPermission('INVOICE', 'READ')")
    @GetMapping("/getAllInvoices")
    public List<Invoice> getALlInvoices() {
        return invoiceService.getAllInvoices();
    }

}
