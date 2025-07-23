package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.User.CouponUsage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CouponUsageRepository extends  MongoRepository<CouponUsage, String> {
    int countByCouponCodeAndUserId(String couponCode, String userId);
}

