package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.Wishlist;

import com.ECommerceApp.Service.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class WishController { // user.

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

    @GetMapping("/removeWishListItem/{productId}")
    public Wishlist removeProductFromWishList(@PathVariable String productId){
        return wishListService.removeFromWishlist("",productId);
    }

    @GetMapping("/clearWishList")
    public String clearWishList(){
        return wishListService.clearWishlist("");
    }

}
