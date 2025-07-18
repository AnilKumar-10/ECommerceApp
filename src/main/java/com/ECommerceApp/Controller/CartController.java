package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.Cart;
import com.ECommerceApp.Model.CartItem;
import com.ECommerceApp.Service.CartService;
import com.ECommerceApp.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class  CartController { //buyer

    @Autowired
    private ProductService productService;
    @Autowired
    private CartService cartService;


    @PostMapping("/insertCart")
    public Cart insertCart(@RequestBody CartItem items){
        items.setPrice(productService.getProductPrice(items.getProductId())* items.getQuantity());
        System.out.println(items);
        return cartService.addItemToCart("USER1038",items);
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
        return cartService.removeOneItemFromCart("",itemNo);
    }

}
