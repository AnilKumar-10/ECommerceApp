package com.ECommerceApp.Service;

import com.ECommerceApp.Model.*;
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
                    newCart.setTotalAmount(0.0);
                    newCart.setUpdatedAt(new Date());
                    return cartRepository.save(newCart);
                });
    }

    // Each time it add one item to the cart.
    public Cart addItemToCart(String buyerId, CartItem item) {
        Cart cart = getCartByBuyerId(buyerId);
        double price = productService.getProductPrice(item.getProductId());
//        item.setPrice(price);
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
            item.setItemId(cart.getItems().size());
            item.setAddedAt(new Date());
            cart.getItems().add(item);
        }
        double totalAmount=0;
        for(CartItem itm :cart.getItems()){
            totalAmount+= itm.getPrice();
        }
        cart.setTotalAmount(totalAmount);
        cart.setUpdatedAt(new Date());
        return cartRepository.save(cart);
    }

    // this will remove one item from the cart.
    public Cart removeOneItemFromCart(String buyerId, String  productId) {
        Cart cart = getCartByBuyerId(buyerId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cart.setUpdatedAt(new Date());
        return cartRepository.save(cart);
    }

    public void removeOrderedItemsFromCart(Order order) {
        System.out.println("inside removecartitem");
        String userId = order.getBuyerId();
        System.out.println("user id: "+userId);
        List<OrderItem> orderItems = order.getOrderItems();
        // Get the Cart
        Cart cart = getCartByBuyerId(userId);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return;
        }
        List<CartItem> cartItems = cart.getItems();
        // Create a filtered list excluding ordered items
        System.out.println("carts: "+cartItems);
        List<CartItem> updatedCartItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            System.out.println("inside for1: "+cartItem);
            boolean matched = false;
            for (OrderItem orderItem : orderItems) {
                System.out.println("inside for2: "+orderItem+"  :  "+cartItem);
                System.out.println( "before "+cartItem.getSize()==(orderItem.getSize()));
                if (cartItem.getProductId().equals(orderItem.getProductId())
                        && cartItem.getSize().equals(orderItem.getSize())
                        && cartItem.getColor().equals(orderItem.getColor())) {
                    System.out.println("inside if");
                    matched = true;
                    cart.setTotalAmount(cart.getTotalAmount() - orderItem.getPrice());
                    break;
                }
            }
            if (!matched) {
                updatedCartItems.add(cartItem);
            }
        }
        // Save updated cart
        cart.setItems(updatedCartItems);
        int i=0;
        for(CartItem cartItem : cart.getItems()){
            cartItem.setItemId(i);
            i++;
        }
        cart.setUpdatedAt(new Date());
        cartRepository.save(cart);
    }

    // Clear all the items in the cart.
    public Cart clearCart(String buyerId) {
        Cart cart = getCartByBuyerId(buyerId);
        cart.setItems(new ArrayList<>());
        cart.setTotalAmount(0);
        cart.setUpdatedAt(new Date());
        return cartRepository.save(cart);
    }


    // moving the cart items to orderItems
    public List<OrderItem> checkOutForOrder(List<Integer> itemIds,String userId){
        List<OrderItem> items = new ArrayList<>();
        for(CartItem item : cartRepository.findByBuyerId(userId).get().getItems()){
            if(itemIds.contains((int)item.getItemId())){
                OrderItem orderItem = new OrderItem();
                BeanUtils.copyProperties(item,orderItem); // BeanUtils.copyProperties(Object source, Object target): Copies all matching properties.
                orderItem.setName(productService.getProductById(item.getProductId()).getName());
                items.add(orderItem);
//                removeItemFromCart(userId,item.getProductId());
            }
        }
        return items;
    }



}
