package com.ECommerceApp.Service;

import com.ECommerceApp.Exceptions.Payment.InvoiceNotFoundException;
import com.ECommerceApp.Exceptions.Payment.PaymentNotFoundException;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Model.Payment.Invoice;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.Repository.InvoiceRepository;
import com.ECommerceApp.Repository.OrderRepository;
import com.ECommerceApp.Repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Optional;

@Slf4j
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
        log.info("Generating the invoice for order: "+orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"SUCCESS".equalsIgnoreCase(order.getPaymentStatus())) {
            log.warn("The payment must be success in order to generate the invoice");
            throw new IllegalStateException("Invoice can only be generated after successful payment");
        }

        // Check if invoice already exists
        Optional<Invoice> existing = invoiceRepository.findByOrderId(orderId);
        if (existing.isPresent()) {
            return existing.get(); // return already generated invoice
        }

        // Get related payment
        Payment payment = paymentRepository.findById(order.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        Invoice invoice = new Invoice();
        invoice.setId(String.valueOf(sequenceGeneratorService.getNextSequence("invoiceId")));
        invoice.setOrderId(orderId);
        invoice.setUserId(order.getBuyerId());
        invoice.setPaymentId(payment.getId());
        invoice.setPaymentMode(order.getPaymentMethod());
        invoice.setAmount(payment.getAmountPaid());
        invoice.setIssuedAt(new Date());
        log.info("invoice is generated");
        return invoiceRepository.save(invoice);
    }

     // 2. Get invoice by ID
    public Invoice getInvoiceById(String invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"));
    }

    //.3. Get invoice by order ID
    public Invoice getInvoiceByOrderId(String orderId) {
        return invoiceRepository.findByOrderId(orderId).get();
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
            throw new InvoiceNotFoundException("Invoice not found");
        }
        invoiceRepository.deleteById(invoiceId);
    }
}
