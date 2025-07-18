package com.ECommerceApp.Service;

import com.ECommerceApp.Exceptions.ProductNotFoundException;
import com.ECommerceApp.Model.Wishlist;
import com.ECommerceApp.Model.WishlistItem;
import com.ECommerceApp.Repository.ProductRepository;
import com.ECommerceApp.Repository.WishListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class WishListService {

    @Autowired
    private WishListRepository wishlistRepository;
    @Autowired
    private ProductRepository productRepository; // optional for validation
    @Autowired
    private CartService cartService;

    // 1. Get wishlist by buyer ID
    public Wishlist getWishlistByBuyerId(String buyerId) {
        return wishlistRepository.findByBuyerId(buyerId)
                .orElseGet(() -> {
                    Wishlist newWishlist = new Wishlist();
                    newWishlist.setBuyerId(buyerId);
                    newWishlist.setItems(new ArrayList<>());
                    newWishlist.setUpdatedAt(new Date());
                    return wishlistRepository.save(newWishlist);
                });
    }

    // 2. Add product to wishlist
    public Wishlist addToWishlist(String buyerId, String productId) {
        Wishlist wishlist = getWishlistByBuyerId(buyerId);

        // Check if product exists
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product does not exist");
        }

        // Check if already in wishlist
        boolean alreadyExists = wishlist.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(productId));

        if (!alreadyExists) {
            WishlistItem item = new WishlistItem();
            item.setProductId(productId);
            item.setAddedAt(new Date());

            wishlist.getItems().add(item);
            wishlist.setUpdatedAt(new Date());

            wishlist = wishlistRepository.save(wishlist);
        }

        return wishlist;
    }

    // 3. Remove product from wishlist
    public Wishlist removeFromWishlist(String buyerId, String productId) {
        Wishlist wishlist = getWishlistByBuyerId(buyerId);

        List<WishlistItem> updatedItems = wishlist.getItems().stream()
                .filter(item -> !item.getProductId().equals(productId))
                .collect(Collectors.toList());

        wishlist.setItems(updatedItems);
        wishlist.setUpdatedAt(new Date());

        return wishlistRepository.save(wishlist);
    }

    // 4. Get all product IDs in wishlist(opt)
    public List<String> getWishlistProductIds(String buyerId) {
        Wishlist wishlist = getWishlistByBuyerId(buyerId);
        return wishlist.getItems().stream()
                .map(WishlistItem::getProductId)
                .collect(Collectors.toList());
    }

    // 5. Check if a product is in wishlist(opt)
    public boolean isInWishlist(String buyerId, String productId) {
        Wishlist wishlist = wishlistRepository.findByBuyerId(buyerId).orElse(null);
        if (wishlist == null) return false;

        return wishlist.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(productId));
    }

    // 6. Clear entire wishlist
    public String clearWishlist(String buyerId) {
        Wishlist wishlist = getWishlistByBuyerId(buyerId);
        wishlist.setItems(new ArrayList<>());
        wishlist.setUpdatedAt(new Date());
        wishlistRepository.save(wishlist);
        return "the wish list is empty now.";
    }



}
