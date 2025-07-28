package com.ECommerceApp.ServiceInterface;

import com.ECommerceApp.Model.User.Cart;
import com.ECommerceApp.Model.User.Wishlist;
import com.ECommerceApp.Model.User.WishlistItem;

import java.util.List;

public interface IWishListService {

    Wishlist getWishlistByBuyerId(String buyerId);

    Wishlist addToWishlist(String buyerId, WishlistItem wishItem);

    Wishlist removeFromWishlist(String buyerId, String productId);

    boolean isInWishlist(String buyerId, String productId);

    String clearWishlist(String buyerId);

    List<String> getWishlistProductIdsByBuyer(String buyerId);

    WishlistItem getWishlistItemByBuyerAndProduct(String buyerId, String productId);

    Cart moveWishTOCart(String userId, String productId);

    Cart cartToWish(String productId, String buyerId);
}

