package com.ECommerceApp.ServiceInterface.User;

import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Model.Order.OrderItem;
import com.ECommerceApp.Model.User.Cart;
import com.ECommerceApp.Model.User.CartItem;

import java.util.List;

public interface ICartService {

    Cart getCartByBuyerId(String buyerId);

    Cart addItemToCart(String buyerId, CartItem item);

    Cart removeOneItemFromCart(String buyerId, String productId);

    void removeOrderedItemsFromCart(Order order);

    Cart clearCart(String buyerId);

    List<OrderItem> checkOutForOrder(List<Integer> itemIds, String userId);
}
