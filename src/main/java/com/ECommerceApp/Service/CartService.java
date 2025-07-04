package com.ECommerceApp.Service;

import com.ECommerceApp.Model.Cart;
import com.ECommerceApp.Model.CartItem;
import com.ECommerceApp.Model.OrderItem;
import com.ECommerceApp.Model.Product;
import com.ECommerceApp.Repository.CartRerepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.ext.LexicalHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRerepository cartRepository;
    @Autowired
    private ProductService productService;

    public Cart getCartByBuyerId(String buyerId) {
        return cartRepository.findByBuyerId(buyerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setBuyerId(buyerId);
                    newCart.setItems(new ArrayList<>());
                    newCart.setUpdatedAt(new Date());
                    return cartRepository.save(newCart);
                });
    }

    public Cart addItemToCart(String buyerId, CartItem item) {
        Cart cart = getCartByBuyerId(buyerId);
        double price = productService.getProductPrice(item.getProductId());
        item.setPrice(price);
        boolean updated = false;
        for (CartItem existingItem : cart.getItems()) {
            if (existingItem.getProductId().equals(item.getProductId()) && existingItem.getSize().equals(item.getSize())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                double total = price * item.getQuantity();
                existingItem.setPrice(total+existingItem.getPrice());
                updated = true;
                break;
            }
        }
        if (!updated) {
            item.setAddedAt(new Date());
            cart.getItems().add(item);
        }
        cart.setUpdatedAt(new Date());
        return cartRepository.save(cart);
    }


    public Cart removeItemFromCart(String buyerId, String productId) {
        Cart cart = getCartByBuyerId(buyerId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cart.setUpdatedAt(new Date());
        return cartRepository.save(cart);
    }

    public Cart clearCart(String buyerId) {
        Cart cart = getCartByBuyerId(buyerId);
        cart.setItems(new ArrayList<>());
        cart.setUpdatedAt(new Date());
        return cartRepository.save(cart);
    }



    public List<OrderItem> checkOutForOrder(List<String> productIds,String userId){
        List<OrderItem> items = new ArrayList<>();
        for(CartItem item : cartRepository.findByBuyerId(userId).get().getItems()){
            if(productIds.contains(item.getProductId())){
                OrderItem orderItem = new OrderItem();
                BeanUtils.copyProperties(orderItem,item);
                items.add(orderItem);
            }
        }
        return items;
    }

}
