package com.ECommerceApp.Controller.Product;

import com.ECommerceApp.Model.Order.Coupon;
import com.ECommerceApp.ServiceInterface.Order.ICouponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/coupon")
public class CouponController { // admin , seller

    @Autowired
    private ICouponService couponService;


    // ADMIN, SELLER
    @PreAuthorize("hasPermission('COUPON', 'INSERT')")
    @PostMapping("/insertCoupon")
    public ResponseEntity<?> insertCoupon(@Valid @RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.createCoupon(coupon));
    }

    // ADMIN, SELLER
    @PreAuthorize("hasPermission('COUPON', 'INSERT')")
    @PostMapping("/insertCoupons")
    public ResponseEntity<?> insertCouponsList(@Valid @RequestBody List<@Valid Coupon> coupon) {
        return ResponseEntity.ok(couponService.createCouponsList(coupon));
    }

    // ADMIN, SELLER
    @PreAuthorize("hasPermission('COUPON', 'UPDATE')")
    @PutMapping("/updateCoupon")
    public ResponseEntity<?> updateCoupon(@Valid @RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.updateCoupon(coupon));
    }

    // ALL ROLES
    @PreAuthorize("hasPermission('COUPON', 'READ')")
    @GetMapping("/getCoupon/{couponCode}")
    public Coupon getCoupon(@PathVariable String couponCode) {
        return couponService.getCouponById(couponCode);
    }

    // ALL ROLES
    @PreAuthorize("hasPermission('COUPON', 'READ')")
    @GetMapping("/getAllCoupons")
    public List<Coupon> getAllCoupons() {
        return couponService.getAllActiveCoupons();
    }
}
