package com.ECommerceApp.Controller.User;

import com.ECommerceApp.Model.User.Cart;
import com.ECommerceApp.Model.User.Wishlist;

import com.ECommerceApp.Model.User.WishlistItem;
import com.ECommerceApp.ServiceInterface.IWishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class WishController { // user.

    @Autowired
    private IWishListService wishListService;

    @GetMapping("/getWish")
    public Wishlist getWish(){
        return  wishListService.getWishlistByBuyerId("USER1019");
    } // user


    @DeleteMapping("/removeWishListItem/{productId}")
    public Wishlist removeProductFromWishList(@PathVariable String productId){
        return wishListService.removeFromWishlist("USER1019",productId);
    }


    @DeleteMapping("/clearWishList")  // user
    public String clearWishList(){
        return wishListService.clearWishlist("USER1019");
    }


    @PostMapping("/addWish")
    public Wishlist insertWish(@RequestBody WishlistItem item){ // user
        return wishListService.addToWishlist("USER1019",item); // there the userId is taken from JWT after implementation
    }


    @PostMapping("/moveToCart/{productId}")
    public Cart moveWishToCart(@PathVariable String productId){
        return wishListService.moveWishTOCart("USER1019",productId);
    }


}
