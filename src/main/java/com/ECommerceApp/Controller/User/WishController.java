package com.ECommerceApp.Controller.User;

import com.ECommerceApp.Model.User.Cart;
import com.ECommerceApp.Model.User.Wishlist;

import com.ECommerceApp.Model.User.WishlistItem;
import com.ECommerceApp.ServiceInterface.IWishListService;
import com.ECommerceApp.Util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishList")
public class WishController { // user.

    @Autowired
    private IWishListService wishListService;

    @GetMapping("/getWish")
    public Wishlist getWish(){
        String userId = new SecurityUtils().getCurrentUserId();
        return  wishListService.getWishlistByBuyerId(userId);
    } // user


    @DeleteMapping("/removeWishListItem/{productId}")
    public Wishlist removeProductFromWishList(@PathVariable String productId){
        String userId = new SecurityUtils().getCurrentUserId();

        return wishListService.removeFromWishlist(userId,productId);
    }


    @DeleteMapping("/clearWishList")  // user
    public String clearWishList(){
        String userId = new SecurityUtils().getCurrentUserId();

        return wishListService.clearWishlist(userId);
    }


    @PostMapping("/addWish")
    public Wishlist insertWish(@RequestBody WishlistItem item){ // user
        String userId = new SecurityUtils().getCurrentUserId();

        return wishListService.addToWishlist(userId,item); // there the userId is taken from JWT after implementation
    }


    @PostMapping("/moveToCart/{productId}")
    public Cart moveWishToCart(@PathVariable String productId){
        String userId = new SecurityUtils().getCurrentUserId();
        return wishListService.moveWishTOCart(userId,productId);
    }


}
