package com.ECommerceApp.ServiceInterface.Payment;

import com.ECommerceApp.Model.Payment.Invoice;

import java.util.List;

public interface IInvoiceService {

    Invoice generateInvoice(String orderId);

    Invoice getInvoiceById(String invoiceId);

    Invoice getInvoiceByOrderId(String orderId);

    List<Invoice> getInvoicesByUserId(String userId);

    List<Invoice> getAllInvoices();

    void deleteInvoice(String invoiceId);
}
