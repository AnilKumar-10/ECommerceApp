package com.ECommerceApp.Controller.User;

import com.ECommerceApp.Model.User.Cart;
import com.ECommerceApp.Model.User.CartItem;
import com.ECommerceApp.Util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ECommerceApp.ServiceInterface.*;
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

    @PreAuthorize("@permissionService.hasPermission('CART', 'INSERT')")
    @PostMapping("/addToCart")
    public Cart addToCart(@RequestBody CartItem items){
        items.setPrice(productService.getProductPrice(items.getProductId())* items.getQuantity());
        System.out.println(items);
        String userId = new SecurityUtils().getCurrentUserId();
        return cartService.addItemToCart(userId,items); // userid is taken from the jwt token
    }

    @PreAuthorize("@permissionService.hasPermission('CART', 'READ')")
    @GetMapping("/getCart")
    public Cart getCart(){
        String id =  new SecurityUtils().getCurrentUserId();
        log.info("getting cart: "+id);
        log.info("Inside /cart/getCart/{}", id);
        return cartService.getCartByBuyerId(id);
    }

    @PreAuthorize("@permissionService.hasPermission('CART', 'DELETE')")
    @PostMapping("/clearCart")
    public Cart clearCart(){
        String userId = new SecurityUtils().getCurrentUserId();
        return cartService.clearCart(userId);
    }

    @PreAuthorize("@permissionService.hasPermission('CART', 'UPDATE')")
    @DeleteMapping("/removeCartItem/{itemNo}")
    public Cart removeItemFromCart(@PathVariable String itemNo){ // we get userid from jwt
        String userId = new SecurityUtils().getCurrentUserId();
        return cartService.removeOneItemFromCart(userId,itemNo);
    }

    @PreAuthorize("@permissionService.hasPermission('CART', 'UPDATE')")
    @PostMapping("/cartToWish/{productId}")
    public Cart moveCartToWish(@PathVariable String productId){
        String userId = new SecurityUtils().getCurrentUserId();
        return wishListService.cartToWish(productId,userId);
    }

    public Cart updateCart(){
        return null;
    }

}
