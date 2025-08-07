package com.ECommerceApp.ServiceImplementation.User;

import com.ECommerceApp.Model.Order.*;
import com.ECommerceApp.Model.User.Cart;
import com.ECommerceApp.Model.User.CartItem;
import com.ECommerceApp.Repository.CartRerepository;
import com.ECommerceApp.ServiceInterface.Product.IProductService;
import com.ECommerceApp.ServiceInterface.User.ICartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class CartService implements ICartService {

    @Autowired
    private CartRerepository cartRepository;
    @Autowired
    private IProductService productService;

    public Cart getCartByBuyerId(String buyerId) {
        log.info("Getting the cart of: "+ buyerId);
        return cartRepository.findByBuyerId(buyerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setBuyerId(buyerId);
                    newCart.setItems(new ArrayList<>());
                    newCart.setTotalAmount(0.0);
                    newCart.setUpdatedAt(new Date());
                    return saveCart(newCart);
                });
    }

    // Each time it add one item to the cart.
    public Cart addItemToCart(String buyerId, CartItem item) {
        Cart cart = getCartByBuyerId(buyerId);
        log.info("adding the cart item: "+item+" to the buyer: "+buyerId);
        double price = productService.getProductPrice(item.getProductId());
        item.setName(productService.getProductById(item.getProductId()).getName());
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
        return saveCart(cart);
    }

    // this will remove one item from the cart.
    public Cart removeOneItemFromCart(String buyerId, String  productId) {
        Cart cart = getCartByBuyerId(buyerId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        double totalAmount=0;
        for(CartItem itm :cart.getItems()){
            totalAmount+= itm.getPrice();
        }
        cart.setTotalAmount(totalAmount);
        int i=0;
        for(CartItem cartItem : cart.getItems()){
            cartItem.setItemId(i);
            i++;
        }
        cart.setUpdatedAt(new Date());
        cart.setUpdatedAt(new Date());
        return saveCart(cart);
    }

    public void removeOrderedItemsFromCart(Order order) {
        log.info("removing the ordered items from the cart after the order delivery.");
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
        List<CartItem> updatedCartItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            boolean matched = false;
            for (OrderItem orderItem : orderItems) {
                if (cartItem.getProductId().equals(orderItem.getProductId())
                        && cartItem.getSize().equals(orderItem.getSize())
                        && cartItem.getColor().equals(orderItem.getColor())) {
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
        saveCart(cart);
    }

    // Clear all the items in the cart.
    public Cart clearCart(String buyerId) {
        Cart cart = getCartByBuyerId(buyerId);
        cart.setItems(new ArrayList<>());
        cart.setTotalAmount(0);
        cart.setUpdatedAt(new Date());
        return saveCart(cart);
    }


    // moving the cart items to orderItems
    public List<OrderItem> checkOutForOrder(List<Integer> itemIds, String userId){
        log.info("adding the cart items: "+itemIds+"  to the order items list");
        List<OrderItem> items = new ArrayList<>();
        for(CartItem item : getCartByBuyerId(userId).getItems()){
            if(itemIds.contains((int)item.getItemId())){
                OrderItem orderItem = new OrderItem();
                BeanUtils.copyProperties(item,orderItem); // BeanUtils.copyProperties(Object source, Object target): Copies all matching properties from source to target.
                items.add(orderItem);
            }
        }
        log.info("order items after adding the cart items : "+items);
        return items;
    }


    public Cart saveCart(Cart cart){
        return cartRepository.save(cart);
    }



}
