package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment,String> {
}
