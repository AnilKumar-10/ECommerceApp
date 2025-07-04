package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.OrderDto;
import com.ECommerceApp.DTO.ProductRequest;
import com.ECommerceApp.DTO.UserRequestDTO;
import com.ECommerceApp.Model.*;
import com.ECommerceApp.Repository.ProductRepository;
import com.ECommerceApp.Repository.ReviewRepository;
import com.ECommerceApp.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CartService cartService;
    @Autowired
    private WishListService wishListService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/home")
    public String getHome(){
        return "welcome";
    }

    @PostMapping("/insertProduct")
    public Product insertProduct(@RequestBody  ProductRequest product){
        return productService.createProduct(product);
    }

    @GetMapping("/getprd/{id}")
    public Product getProduct(@PathVariable String id){
        return productService.getProductById(id);
    }

    @GetMapping("/get")
    public List<Product> get(){
        return productService.getAllProducts();
    }

    @PostMapping("/insertUser")
    public Users insertUser(@RequestBody Users users){
        return  userService.registerUser(users);
    }

    @GetMapping("/users")
    public List<Users> getAllUsers(){
       return userService.getAllUsers();
    }

    @PostMapping("/insertAddress")
    public Address insertUserAddress(@RequestBody Address address){
        return  addressService.createAddress(address);
    }

    @GetMapping("/getAddress/{id}")
    public List<Address> getUserAddress(@PathVariable String id){
        return addressService.getAddressesByUserId(id);
    }

    @PostMapping("/insertCart")
    public Cart insertCart(@RequestBody CartItem items){
        return cartService.addItemToCart("USER1002",items);
    }

    @GetMapping("/getcart/{id}")
    public Cart getCart(@PathVariable String id){
        return cartService.getCartByBuyerId(id);
    }

    @PostMapping("/postReview")
    public Review postReview(@RequestBody Review review){
        return reviewService.addReview(review);
    }

    @GetMapping("/getReview/{id}")
    public List<Review> getProductReview(@PathVariable String id){
        return reviewService.getReviewByProductId(id);
    }

    @PostMapping("/addWish")
    public Wishlist insertWish(@RequestBody Map<String,String > pid ){
        return wishListService.addToWishlist("USER1002",pid.get("id"));
    }

    @GetMapping("/getWish")
    public Wishlist getWish(){
        return  wishListService.getWishlistByBuyerId("USER1002");
    }


    @PostMapping("/checkout")
    public Order check(@RequestBody OrderDto orderDto){
        Order order = orderService.createOrder(orderDto);
        return null;
    }


}

