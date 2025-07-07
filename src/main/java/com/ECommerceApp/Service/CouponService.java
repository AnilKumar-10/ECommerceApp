package com.ECommerceApp.Service;

import com.ECommerceApp.Exceptions.CouponNotFoundException;
import com.ECommerceApp.Exceptions.InValidCouponException;
import com.ECommerceApp.Model.Coupon;
import com.ECommerceApp.Model.CouponUsage;
import com.ECommerceApp.Repository.CouponRepository;
import com.ECommerceApp.Repository.CouponUsageRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CouponService {
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponUsageRepository couponUsageRepository;

    // 1. Create a new coupon
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    // 2. Update an existing coupon
    public Coupon updateCoupon(String couponId, Coupon updatedCoupon) {
        Coupon existing = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));
//        existing.setCode(updatedCoupon.getCode());
//        existing.setDiscountType(updatedCoupon.getDiscountType());
//        existing.setDiscountValue(updatedCoupon.getDiscountValue());
//        existing.setMinOrderValue(updatedCoupon.getMinOrderValue());
//        existing.setMaxUsagePerUser(updatedCoupon.getMaxUsagePerUser());
//        existing.setValidFrom(updatedCoupon.getValidFrom());
//        existing.setValidTo(updatedCoupon.getValidTo());
//        existing.setActive(updatedCoupon.isActive());

        BeanUtils.copyProperties(updatedCoupon,existing);

        return couponRepository.save(existing);
    }

    // 3. Activate or deactivate a coupon
    public Coupon toggleCoupon(String couponId, boolean isActive) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));
        coupon.setActive(isActive);
        return couponRepository.save(coupon);
    }

    // 4. Get coupon by ID
    public Coupon getCouponById(String couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));
    }

    // 5. Get all active coupons
    public List<Coupon> getAllActiveCoupons() {
        return couponRepository.findByIsActiveTrue();
    }

    // 6. Validate coupon entered by customer
    public Coupon validateCoupon(String code, String userId, double orderAmount) {
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new InValidCouponException("Invalid or inactive coupon code"));
        System.out.println(coupon.getMinOrderValue()+"  :  "+orderAmount);
        Date now = new Date();
        if (now.before(coupon.getValidFrom()) || now.after(coupon.getValidTo())) {
            throw new InValidCouponException("Coupon is not valid for current date");
        }

        if (orderAmount < coupon.getMinOrderValue()) {
            throw new InValidCouponException("Order value does not meet minimum required for this coupon");
        }

        int usageCount = couponUsageRepository.countByCouponCodeAndUserId(code, userId);
        if (usageCount >= coupon.getMaxUsagePerUser()) {
            throw new InValidCouponException("Coupon usage limit exceeded for this user");
        }
        return coupon;
    }

    public double getDiscountAmount(Coupon coupon, double totalAmount) {
        double discount = 0.0;

        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = (coupon.getDiscountValue() / 100.0) * totalAmount;
        } else if ("FLAT".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = coupon.getDiscountValue();
        }
        // Discount doesn't exceed the total amount
        return Math.min(discount, totalAmount);
    }


    public void recordCouponUsage(String code, String userId) {
        CouponUsage usage = new CouponUsage();
        usage.setCouponCode(code);
        usage.setUserId(userId);
        usage.setUsedAt(new Date());
        couponUsageRepository.save(usage);
    }
}
