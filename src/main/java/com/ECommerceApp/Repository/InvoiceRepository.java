package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvoiceRepository extends MongoRepository<Invoice,String> {
}
