package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.Wishlist;
import com.ECommerceApp.Service.WishListService;
import jakarta.validation.constraints.AssertFalse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WishController {

    @Autowired
    private WishListService wishListService;

    @PostMapping("/addWish")
    public Wishlist insertWish(@RequestBody Map<String,String > pid ){
        return wishListService.addToWishlist("USER1002",pid.get("id"));
    }

    @GetMapping("/getWish")
    public Wishlist getWish(){
        return  wishListService.getWishlistByBuyerId("USER1002");
    }

}
