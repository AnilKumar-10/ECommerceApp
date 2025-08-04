package com.ECommerceApp.Controller.User;

import com.ECommerceApp.Model.User.Cart;
import com.ECommerceApp.Model.User.Wishlist;

import com.ECommerceApp.Model.User.WishlistItem;
import com.ECommerceApp.ServiceInterface.IWishListService;
import com.ECommerceApp.Util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishList")
public class WishController { // user.

    @Autowired
    private IWishListService wishListService;


    // ✅ USER (READ) — SELF
    @PreAuthorize("hasPermission('WISHLIST', 'READ')")
    @GetMapping("/getWish")
    public Wishlist getWish() {
        String userId = new SecurityUtils().getCurrentUserId();
        return wishListService.getWishlistByBuyerId(userId);
    }

    // ✅ USER (DELETE) — SELF
    @PreAuthorize("hasPermission('WISHLIST', 'DELETE')")
    @DeleteMapping("/removeWishListItem/{productId}")
    public Wishlist removeProductFromWishList(@PathVariable String productId) {
        String userId = new SecurityUtils().getCurrentUserId();
        return wishListService.removeFromWishlist(userId, productId);
    }

    // ✅ USER (DELETE) — SELF
    @PreAuthorize("hasPermission('WISHLIST', 'DELETE')")
    @DeleteMapping("/clearWishList")
    public String clearWishList() {
        String userId = new SecurityUtils().getCurrentUserId();
        return wishListService.clearWishlist(userId);
    }

    // ✅ USER (INSERT) — SELF
    @PreAuthorize("hasPermission('WISHLIST', 'INSERT')")
    @PostMapping("/addWish")
    public Wishlist insertWish(@RequestBody WishlistItem item) {
        String userId = new SecurityUtils().getCurrentUserId();
        return wishListService.addToWishlist(userId, item);
    }

    // ✅ USER (UPDATE) — SELF
    @PreAuthorize("hasPermission('WISHLIST', 'UPDATE')")
    @PostMapping("/moveToCart/{productId}")
    public Cart moveWishToCart(@PathVariable String productId) {
        String userId = new SecurityUtils().getCurrentUserId();
        return wishListService.moveWishTOCart(userId, productId);
    }
}
