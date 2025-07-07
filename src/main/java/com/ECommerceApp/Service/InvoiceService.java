package com.ECommerceApp.Service;

import com.ECommerceApp.Model.Invoice;
import com.ECommerceApp.Model.Order;
import com.ECommerceApp.Model.Payment;
import com.ECommerceApp.Repository.InvoiceRepository;
import com.ECommerceApp.Repository.OrderRepository;
import com.ECommerceApp.Repository.PaymentRepository;
import com.ECommerceApp.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;


    public Invoice generateInvoice(String orderId) {
        // Validate order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"SUCCESS".equalsIgnoreCase(order.getPaymentStatus())) {
            throw new IllegalStateException("Invoice can only be generated after successful payment");
        }

        // Check if invoice already exists
        Optional<Invoice> existing = invoiceRepository.findByOrderId(orderId);
        if (existing.isPresent()) {
            return existing.get(); // return already generated invoice
        }

        // Get related payment
        Payment payment = paymentRepository.findById(order.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        Invoice invoice = new Invoice();
        invoice.setId(String.valueOf(sequenceGeneratorService.getNextSequence("invoiceId")));
        invoice.setOrderId(orderId);
        invoice.setUserId(order.getBuyerId());
        invoice.setPaymentId(payment.getId());
        invoice.setPaymentMode(order.getPaymentMethod());
        invoice.setAmount(payment.getAmountPaid());
        invoice.setIssuedAt(new Date());

        return invoiceRepository.save(invoice);
    }

     // 2. Get invoice by ID
    public Invoice getInvoiceById(String invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    //.3. Get invoice by order ID
    public Optional<Invoice> getInvoiceByOrderId(String orderId) {
        return invoiceRepository.findByOrderId(orderId);
    }


     //4 Get all invoices for a use
    public List<Invoice> getInvoicesByUserId(String userId) {
        return invoiceRepository.findByUserId(userId);
    }


     // 5. Get all invoice
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }


     //6. Delete an invoice (admin only - optional)
    public void deleteInvoice(String invoiceId) {
        if (!invoiceRepository.existsById(invoiceId)) {
            throw new RuntimeException("Invoice not found");
        }
        invoiceRepository.deleteById(invoiceId);
    }
}
