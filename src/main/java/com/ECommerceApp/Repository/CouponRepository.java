package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Coupon;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends MongoRepository<Coupon,String> {
    Optional<Coupon> findByCodeAndIsActiveTrue(String code);
    List<Coupon> findByIsActiveTrue();
    Optional<Coupon> findByCode(String code);
}
