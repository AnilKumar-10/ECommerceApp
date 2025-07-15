package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.Coupon;
import com.ECommerceApp.Service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/insertCoupon")
    public Coupon insertCoupon(@RequestBody Coupon coupon){
        return couponService.createCoupon(coupon);
    }


}
