package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Payment.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment,String> {

    @Query("{'status':'FAILED'}")
    List<Payment> findAllFailedPayements();


}
