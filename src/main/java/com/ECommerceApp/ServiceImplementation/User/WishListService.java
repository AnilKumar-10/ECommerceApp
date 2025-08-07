package com.ECommerceApp.ServiceImplementation.User;

import com.ECommerceApp.Exceptions.Product.ProductNotFoundException;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.Model.User.*;
import com.ECommerceApp.Repository.Product.ProductRepository;
import com.ECommerceApp.Repository.User.WishListRepository;
import com.ECommerceApp.ServiceInterface.User.IWishListService;
import com.ECommerceApp.ServiceInterface.Product.IProductService;
import com.ECommerceApp.ServiceInterface.User.ICartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WishListService implements IWishListService {

    @Autowired
    private WishListRepository wishlistRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ICartService cartService;
    @Autowired
    private IProductService productService;

    //  Get wishlist by buyer ID
    public Wishlist getWishlistByBuyerId(String buyerId) {
        log.info("getting wish list of user: "+buyerId);
        return wishlistRepository.findByBuyerId(buyerId)
                .orElseGet(() -> {
                    Wishlist newWishlist = new Wishlist();
                    newWishlist.setBuyerId(buyerId);
                    newWishlist.setItems(new ArrayList<>());
                    newWishlist.setUpdatedAt(new Date());
                    return wishlistRepository.save(newWishlist);
                });
    }

    //  Add product to wishlist
    public Wishlist addToWishlist(String buyerId, WishlistItem wishItem) {
        log.info("Adding the new product to the wish list");
        Wishlist wishlist = getWishlistByBuyerId(buyerId);

        // Check if product exists
        if (!productRepository.existsById(wishItem.getProductId())) {
            throw new ProductNotFoundException("Product does not exist");
        }

        // Check if already in wishlist
        boolean alreadyExists = isInWishlist(buyerId, wishItem.getProductId());
        if (!alreadyExists) {
            Product product = productService.getProductById(wishItem.getProductId());
            wishItem.setAvailable(product.isAvailable());
            wishItem.setName(product.getName());
            wishItem.setPrice(product.getPrice()*wishItem.getQuantity());
            wishItem.setAddedAt(new Date());
            wishlist.setUpdatedAt(new Date());
            wishlist.getItems().add(wishItem);
            wishlist = wishlistRepository.save(wishlist);
        }

        return wishlist;
    }

    //  Remove product from wishlist
    public Wishlist removeFromWishlist(String buyerId, String productId) {
        log.info("Removing the product from the wish list: "+productId);
        Wishlist wishlist = getWishlistByBuyerId(buyerId);

        List<WishlistItem> updatedItems = wishlist.getItems().stream()
                .filter(item -> !item.getProductId().equals(productId))
                .collect(Collectors.toList());

        wishlist.setItems(updatedItems);
        wishlist.setUpdatedAt(new Date());

        return wishlistRepository.save(wishlist);
    }

    //  Check if a product is in wishlist(opt)
    public boolean isInWishlist(String buyerId, String productId) {
        Wishlist wishlist = wishlistRepository.findByBuyerId(buyerId).orElse(null);
        if (wishlist == null) return false;

        return wishlist.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(productId));
    }

    //  Clear entire wishlist
    public String clearWishlist(String buyerId) {
        log.warn("Clearing the wish list of: "+buyerId);
        Wishlist wishlist = getWishlistByBuyerId(buyerId);
        wishlist.setItems(new ArrayList<>());
        wishlist.setUpdatedAt(new Date());
        wishlistRepository.save(wishlist);
        return "the wish list is empty now.";
    }

    public List<String> getWishlistProductIdsByBuyer(String buyerId) {
        Wishlist wishlist = wishlistRepository.findByBuyerId(buyerId).get();
        if (wishlist.getItems() != null) {
            return wishlist.getItems()
                    .stream()
                    .map(WishlistItem::getProductId)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    public WishlistItem getWishlistItemByBuyerAndProduct(String buyerId, String productId) {
        Wishlist wishlist = wishlistRepository.findByBuyerId(buyerId).get();
        if (wishlist != null && wishlist.getItems() != null) {
            return wishlist.getItems()
                    .stream()
                    .filter(item -> productId.equals(item.getProductId()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }



    public Cart moveWishTOCart(String userId,String productId){
        log.info("Moving the: "+productId+ " from wish to Cart of: "+userId);
        WishlistItem item = getWishlistItemByBuyerAndProduct(userId,productId);
        CartItem cartItem = new CartItem();
        BeanUtils.copyProperties(item,cartItem);
        return cartService.addItemToCart(userId,cartItem);
    }


    public Cart cartToWish(String productId, String buyerId){
        log.info("Moving the: "+productId+ " from Cart to Wish list of: "+buyerId);
        WishlistItem wishlistItem = new WishlistItem();
        Cart cart = cartService.getCartByBuyerId(buyerId);
        CartItem cartItem = cart.getItems()
                .stream()
                .filter(item -> productId.equals(item.getProductId()))
                .findFirst()
                .orElse(null);
        if (cartItem==null) {
            throw new ProductNotFoundException("Product not found in cart: " + productId);
        }

        BeanUtils.copyProperties(cartItem,wishlistItem);
        addToWishlist(buyerId,wishlistItem);
        return cartService.removeOneItemFromCart(buyerId,productId);
    }




}
