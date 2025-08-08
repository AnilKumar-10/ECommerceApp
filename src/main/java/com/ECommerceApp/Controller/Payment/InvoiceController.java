package com.ECommerceApp.Controller.Payment;


import com.ECommerceApp.Model.Payment.Invoice;
import com.ECommerceApp.ServiceImplementation.Payment.InvoiceService;
import com.ECommerceApp.ServiceInterface.Order.IOrderService;
import com.ECommerceApp.Util.OwnershipGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @Autowired
    private IOrderService orderService;

    //  BUYER (self) — reads own invoice by orderId
    @PreAuthorize("hasPermission('INVOICE', 'READ')")
    @GetMapping("/genInvoice/{orderId}")
    public ResponseEntity<?> generateInvoice(@PathVariable String orderId) {
        return ResponseEntity.ok(invoiceService.generateInvoice(orderId));
    }

    //  ADMIN — reads invoice by ID
    @PreAuthorize("hasPermission('INVOICE', 'READ')")
    @GetMapping("/getInvoiceById/{invoiceId}")
    public ResponseEntity<?> getInVoice(@PathVariable String invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }

    //  ADMIN — reads invoice by order
    @PreAuthorize("hasPermission('INVOICE', 'READ')")
    @GetMapping("/getInvoiceByOrder/{orderId}")
    public ResponseEntity<?> getInVoiceByOrder(@PathVariable String orderId) {
        new OwnershipGuard().checkSelf(orderService.getOrder(orderId).getBuyerId());
        return ResponseEntity.ok(invoiceService.getInvoiceByOrderId(orderId));
    }

    //  BUYER (self) or ADMIN — gets all invoices for specific user
    @PreAuthorize("hasPermission(#userId, 'com.ECommerceApp.Model.User', 'READ')")
    @GetMapping("/getInvoiceByUser/{userId}")
    public ResponseEntity<?> getAllInvoicesByUser(@PathVariable String userId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByUserId(userId));
    }

    //  ADMIN — gets all invoices
    @PreAuthorize("hasPermission('INVOICE', 'READ')")
    @GetMapping("/getAllInvoices")
    public ResponseEntity<?> getALlInvoices() {
        new OwnershipGuard().checkAdmin();
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

}
