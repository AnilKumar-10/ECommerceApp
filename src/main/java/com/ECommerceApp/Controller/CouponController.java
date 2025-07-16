package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.Category;
import com.ECommerceApp.Model.Coupon;
import com.ECommerceApp.Service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/insertCoupon")
    public Coupon insertCoupon(@RequestBody Coupon coupon){
        return couponService.createCoupon(coupon);
    }

    @GetMapping("/updateCoupon")
    public Coupon updateCoupon(@RequestBody Coupon coupon){
        return couponService.updateCoupon(coupon);
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
