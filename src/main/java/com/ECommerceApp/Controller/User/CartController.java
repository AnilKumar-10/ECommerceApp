package com.ECommerceApp.Controller.User;

import com.ECommerceApp.Model.User.Cart;
import com.ECommerceApp.Model.User.CartItem;
import com.ECommerceApp.Util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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


    @PostMapping("/addToCart")
    public Cart addToCart(@RequestBody CartItem items){
        items.setPrice(productService.getProductPrice(items.getProductId())* items.getQuantity());
        System.out.println(items);
        String userId = SecurityUtils.getCurrentUserId();
        return cartService.addItemToCart(userId,items); // userid is taken from the jwt token
    }


    @GetMapping("/getCart")
    public Cart getCart(){
        String id =  SecurityUtils.getCurrentUserId();
        log.info("getting cart: "+id);
        log.info("Inside /cart/getCart/{}", id);
        return cartService.getCartByBuyerId(id);
    }


    @PostMapping("/clearCart")
    public Cart clearCart(){
        String userId = SecurityUtils.getCurrentUserId();
        return cartService.clearCart(userId);
    }


    @DeleteMapping("/removeCartItem/{itemNo}")
    public Cart removeItemFromCart(@PathVariable String itemNo){ // we get userid from jwt
        String userId = SecurityUtils.getCurrentUserId();
        return cartService.removeOneItemFromCart(userId,itemNo);
    }


    @PostMapping("/cartToWish/{productId}")
    public Cart moveCartToWish(@PathVariable String productId){
        String userId = SecurityUtils.getCurrentUserId();
        return wishListService.cartToWish(productId,userId);
    }

    public Cart updateCart(){
        return null;
    }

}
