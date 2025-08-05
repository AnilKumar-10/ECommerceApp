package com.ECommerceApp.Controller.User;

import com.ECommerceApp.Model.User.Cart;
import com.ECommerceApp.Model.User.CartItem;
import com.ECommerceApp.ServiceInterface.Product.IProductService;
import com.ECommerceApp.ServiceInterface.User.ICartService;
import com.ECommerceApp.ServiceInterface.User.IWishListService;
import com.ECommerceApp.Util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/cart")
public class  CartController { //buyer

    @Autowired
    private IProductService productService;
    @Autowired
    private ICartService cartService;
    @Autowired
    private IWishListService wishListService;

    @PreAuthorize("hasPermission('CART', 'INSERT')")
    @PostMapping("/addToCart")
    public Cart addToCart(@RequestBody CartItem items){
        items.setPrice(productService.getProductPrice(items.getProductId())* items.getQuantity());
        System.out.println(items);
        String userId = new SecurityUtils().getCurrentUserId();
        return cartService.addItemToCart(userId,items);
    }

    @PreAuthorize("hasPermission(#userId, 'com.ECommerceApp.Model.User', 'READ')")
    @GetMapping("/getCart/{userId}")
    public Cart getCart(@PathVariable String userId){
        return cartService.getCartByBuyerId(userId);
    }

    @PreAuthorize("hasPermission(#userId, 'com.ECommerceApp.Model.User', 'DELETE')")
    @PostMapping("/clearCart/{userId}")
    public Cart clearCart(@PathVariable String userId){
        return cartService.clearCart(userId);
    }

    @PreAuthorize("hasPermission('CART', 'UPDATE')")
    @DeleteMapping("/removeCartItem/{itemNo}")
    public Cart removeItemFromCart(@PathVariable String itemNo){
        String userId = new SecurityUtils().getCurrentUserId();
        return cartService.removeOneItemFromCart(userId,itemNo);
    }

    @PreAuthorize("hasPermission('CART', 'UPDATE')")
    @PostMapping("/cartToWish/{productId}")
    public Cart moveCartToWish(@PathVariable String productId){
        String userId = new SecurityUtils().getCurrentUserId();
        return wishListService.cartToWish(productId,userId);
    }


}
