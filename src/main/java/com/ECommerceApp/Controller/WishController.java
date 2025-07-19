package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.Wishlist;

import com.ECommerceApp.Service.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class WishController { // user.

    @Autowired
    private WishListService wishListService;

    @PostMapping("/addWish")
    public Wishlist insertWish(@RequestBody Map<String,String > pid ){
        return wishListService.addToWishlist("USER1002",pid.get("id")); // there the userId is taken from JWT after implementation
    }

    @GetMapping("/getWish")
    public Wishlist getWish(){
        return  wishListService.getWishlistByBuyerId("USER1002");
    } // user

    @GetMapping("/removeWishListItem/{productId}")
    public Wishlist removeProductFromWishList(@PathVariable String productId){
        return wishListService.removeFromWishlist("",productId);
    }

    @GetMapping("/clearWishList")  // user
    public String clearWishList(){
        return wishListService.clearWishlist("");
    }

}
