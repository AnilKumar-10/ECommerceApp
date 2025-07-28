package com.ECommerceApp.Controller.User;

import com.ECommerceApp.Model.User.Cart;
import com.ECommerceApp.Model.User.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ECommerceApp.ServiceInterface.*;

@RestController
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
        return cartService.addItemToCart("USER1040",items); // userid is taken from the jwt token
    }


    @GetMapping("/getCart/{id}")
    public Cart getCart(@PathVariable String id){
        return cartService.getCartByBuyerId(id);
    }


    @PostMapping("/clearCart/{userId}")
    public Cart clearCart(@PathVariable String userId){
        return cartService.clearCart(userId);
    }


    @DeleteMapping("/removeCartItem")
    public Cart removeItemFromCart(@PathVariable String itemNo){ // we get userid from jwt
        return cartService.removeOneItemFromCart("USER1040",itemNo);
    }


    @PostMapping("/cartToWish/{productId}")
    public Cart moveCartToWish(@PathVariable String productId){
        return wishListService.cartToWish(productId,"USER1040");
    }


    public Cart updateCart(){
        return null;
    }

}
