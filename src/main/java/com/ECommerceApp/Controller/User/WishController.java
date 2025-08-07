package com.ECommerceApp.Controller.User;

import com.ECommerceApp.Model.User.Cart;
import com.ECommerceApp.Model.User.Wishlist;

import com.ECommerceApp.Model.User.WishlistItem;
import com.ECommerceApp.ServiceInterface.User.IWishListService;
import com.ECommerceApp.Util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getWish() {
        String userId = new SecurityUtils().getCurrentUserId();
        return ResponseEntity.ok(wishListService.getWishlistByBuyerId(userId));
    }

    // ✅ USER (DELETE) — SELF
    @PreAuthorize("hasPermission('WISHLIST', 'DELETE')")
    @DeleteMapping("/removeWishListItem/{productId}")
    public ResponseEntity<?> removeProductFromWishList(@PathVariable String productId) {
        String userId = new SecurityUtils().getCurrentUserId();
        return ResponseEntity.ok(wishListService.removeFromWishlist(userId, productId));
    }

    // ✅ USER (DELETE) — SELF
    @PreAuthorize("hasPermission('WISHLIST', 'DELETE')")
    @DeleteMapping("/clearWishList")
    public ResponseEntity<?> clearWishList() {
        String userId = new SecurityUtils().getCurrentUserId();
        return ResponseEntity.ok(wishListService.clearWishlist(userId));
    }

    // ✅ USER (INSERT) — SELF
    @PreAuthorize("hasPermission('WISHLIST', 'INSERT')")
    @PostMapping("/addWish")
    public ResponseEntity<?> insertWish(@RequestBody WishlistItem item) {
        String userId = new SecurityUtils().getCurrentUserId();
        return ResponseEntity.ok(wishListService.addToWishlist(userId, item));
    }

    // ✅ USER (UPDATE) — SELF
    @PreAuthorize("hasPermission('WISHLIST', 'UPDATE')")
    @PostMapping("/moveToCart/{productId}")
    public ResponseEntity<?> moveWishToCart(@PathVariable String productId) {
        String userId = new SecurityUtils().getCurrentUserId();
        return ResponseEntity.ok(wishListService.moveWishTOCart(userId, productId));
    }
}
