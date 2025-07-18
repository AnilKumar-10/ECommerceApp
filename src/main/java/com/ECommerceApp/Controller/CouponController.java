package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.Coupon;
import com.ECommerceApp.Service.CouponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CouponController { // admin , seller

    @Autowired
    private CouponService couponService;

    @PostMapping("/insertCoupon")
    public ResponseEntity<?> insertCoupon(@Valid @RequestBody Coupon coupon){
        return ResponseEntity.ok(couponService.createCoupon(coupon));
    }

    @PostMapping("/insertCoupons")
    public ResponseEntity<?>  insertCouponsList(@Valid @RequestBody List<@Valid Coupon> coupon){
        return ResponseEntity.ok(couponService.createCouponsList(coupon));
    }


    @GetMapping("/updateCoupon")
    public ResponseEntity<?> updateCoupon(@Valid @RequestBody Coupon coupon){
        return ResponseEntity.ok(couponService.updateCoupon(coupon));
    }

    @GetMapping("/getCoupon/{couponCode}")
    public Coupon getCoupon(@PathVariable String couponCode){
        return couponService.getCouponById(couponCode);
    }

    @GetMapping("/getAllCoupons")
    public List<Coupon> getAllCoupons(){
        return couponService.getAllActiveCoupons();
    }
}
