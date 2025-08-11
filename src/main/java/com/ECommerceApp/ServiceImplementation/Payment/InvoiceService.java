package com.ECommerceApp.ServiceImplementation.Payment;

import com.ECommerceApp.Exceptions.Payment.InvoiceNotFoundException;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Model.Payment.Invoice;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.Repository.InvoiceRepository;
import com.ECommerceApp.ServiceImplementation.Order.SequenceGeneratorService;
import com.ECommerceApp.ServiceInterface.Payment.IInvoiceService;
import com.ECommerceApp.ServiceInterface.Order.IOrderService;
import com.ECommerceApp.ServiceInterface.Payment.IPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Optional;

@Slf4j
@Service
public class InvoiceService implements IInvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IPaymentService paymentService;

    public Invoice generateInvoice(String orderId) {
        // Validate order
        log.info("Generating the invoice for order: {}", orderId);
        Order order = orderService.getOrder(orderId);

        if (!(order.getPaymentStatus()== Payment.PaymentStatus.SUCCESS)) {
            log.warn("The payment status must be success in order to generate the invoice");
            throw new IllegalStateException("Invoice can only be generated after successful payment");
        }

        // Check if invoice already exists
        Optional<Invoice> existing = invoiceRepository.findByOrderId(orderId);
        if (existing.isPresent()) {
            return existing.get(); // return already generated invoice
        }

        // Get related payment
        Payment payment = paymentService.getPaymentById(order.getPaymentId());

        Invoice invoice = new Invoice();
        invoice.setId(String.valueOf(sequenceGeneratorService.getNextSequence("invoiceId")));
        invoice.setOrderId(orderId);
        invoice.setUserId(order.getBuyerId());
        invoice.setPaymentId(payment.getId());
        invoice.setPaymentMode(order.getPaymentMethod().name());
        invoice.setAmount(payment.getAmountPaid());
        invoice.setIssuedAt(new Date());
        log.info("invoice is generated");
        return invoiceRepository.save(invoice);
    }

     // Get invoice by ID
    public Invoice getInvoiceById(String invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"));
    }

    // Get invoice by order ID
    public Invoice getInvoiceByOrderId(String orderId) {
        return invoiceRepository.findByOrderId(orderId).orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"));
    }


     // Get all invoices for a use
    public List<Invoice> getInvoicesByUserId(String userId) {
        return invoiceRepository.findByUserId(userId);
    }


     //  Get all invoice
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }


     // Delete an invoice (admin only - optional)
    public void deleteInvoice(String invoiceId) {
        if (!invoiceRepository.existsById(invoiceId)) {
            throw new InvoiceNotFoundException("Invoice not found");
        }
        invoiceRepository.deleteById(invoiceId);
    }
}
