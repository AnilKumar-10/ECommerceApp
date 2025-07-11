package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.*;

public interface InvoiceRepository extends MongoRepository<Invoice,String> {
    Optional<Invoice> findByOrderId(String orderId);
    List<Invoice> findByUserId(String userId);
}
