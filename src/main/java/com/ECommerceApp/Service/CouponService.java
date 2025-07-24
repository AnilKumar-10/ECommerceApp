package com.ECommerceApp.Service;

import com.ECommerceApp.Exceptions.Order.CouponNotFoundException;
import com.ECommerceApp.Exceptions.Order.InValidCouponException;
import com.ECommerceApp.Model.Order.Coupon;
import com.ECommerceApp.Model.User.CouponUsage;
import com.ECommerceApp.Repository.CouponRepository;
import com.ECommerceApp.Repository.CouponUsageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class CouponService {
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponUsageRepository couponUsageRepository;

    // 1. Create a new coupon
    public Coupon createCoupon(Coupon coupon) {
        log.info("inserting the coupon details: "+coupon);
        return couponRepository.save(coupon);
    }

    // 2. Update an existing coupon
    public Coupon updateCoupon(Coupon updatedCoupon) {
        Coupon existing = couponRepository.findById(updatedCoupon.getId())
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));
        BeanUtils.copyProperties(existing,updatedCoupon);

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
    public Coupon getCouponById(String couponCode) {
        return couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));
    }

    // 5. Get all active coupons
    public List<Coupon> getAllActiveCoupons() {
        return couponRepository.findByIsActiveTrue();
    }

    // 6. Validate coupon entered by customer
    public Coupon validateCoupon(String code, String userId, double orderAmount) {
        log.info("validating the coupon code: "+code);
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new InValidCouponException("Invalid or inactive coupon code"));
        System.out.println(coupon.getMinOrderValue()+"  :  "+orderAmount);
        Date now = new Date();
        if (now.before(coupon.getValidFrom()) || now.after(coupon.getValidTo())) {
            log.warn("The coupon is expired");
            throw new InValidCouponException("Coupon is not valid for current date");
        }

        if (orderAmount < coupon.getMinOrderValue()) {
            log.warn("Order value doesnt meet the minimum required amount");
            throw new InValidCouponException("Order value does not meet minimum required for this coupon");
        }

        int usageCount = couponUsageRepository.countByCouponCodeAndUserId(code, userId);
        if (usageCount > coupon.getMaxUsagePerUser()) {
            log.warn("User already used the coupon.");
            throw new InValidCouponException("Coupon usage limit exceeded for this user");
        }
        log.info("Provided coupon is valid coupon");
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

    public String  createCouponsList(List<Coupon> coupons) {
        int c=0;
        for(Coupon coupon : coupons){
            couponRepository.save(coupon);
            c++;
        }
        return "Insertion of coupons are done successfully: "+c;

    }
}
