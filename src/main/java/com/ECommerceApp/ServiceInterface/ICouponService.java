package com.ECommerceApp.ServiceInterface;

import com.ECommerceApp.Model.Order.Coupon;

import java.util.List;

public interface ICouponService {

    Coupon createCoupon(Coupon coupon);

    String createCouponsList(List<Coupon> coupons);

    Coupon updateCoupon(Coupon updatedCoupon);

    Coupon toggleCoupon(String couponId, boolean isActive);

    Coupon getCouponById(String couponCode);

    List<Coupon> getAllActiveCoupons();

    Coupon validateCoupon(String code, String userId, double orderAmount);

    double getDiscountAmount(Coupon coupon, double totalAmount);

    void recordCouponUsage(String code, String userId);
}
