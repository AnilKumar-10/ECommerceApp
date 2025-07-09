package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.*;
import com.ECommerceApp.Model.*;
import com.ECommerceApp.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    @Autowired
    private CouponService couponService;
//    @Autowired
//    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private ShippingService shippingService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private ReturnService returnService ;
    @Autowired
    private StockLogService stockLogService;


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
        items.setPrice(productService.getProductPrice(items.getProductId())* items.getQuantity());
        System.out.println(items);
        return cartService.addItemToCart("USER1005",items);
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


    @PostMapping("/insertCoupon")
    public Coupon insertCoupon(@RequestBody Coupon coupon){
        return couponService.createCoupon(coupon);
    }

    @PostMapping("/placeOrder")
    public Order placeOrder(@RequestBody OrderDto orderDto){
        return orderService.createOrder(orderDto);
    }

    @PostMapping("/initpay")
    public Payment initiatePaymentDto(@RequestBody InitiatePaymentDto initiatePaymentDto){
        return paymentService.initiatePayment(initiatePaymentDto);
    }

    @PostMapping("/pay")
    public Payment pay(@RequestBody PaymentDto paymentDto){
        return paymentDto.getStatus().equalsIgnoreCase("Success")?paymentService.confirmPayment(paymentDto):paymentService.failPayment(paymentDto);
    }

    @GetMapping("/getOrder/{id}")
    public Order getOrder(@PathVariable String id){
        return orderService.getOrder(id);
    }

    @PostMapping("/insertDelivery")
    public DeliveryPerson insertDelivery(@RequestBody DeliveryPerson deliveryPerson){
        return  deliveryService.register(deliveryPerson);
    }

    @GetMapping("/getShipping/{id}")
    public ShippingDetails getShipping(@PathVariable String id){
        return shippingService.getShippingByOrderId(id);
    }

    @PostMapping("/updateShipping")
    public ShippingDetails updateShip(@RequestBody  ShippingUpdateDTO shippingUpdateDTO){
        return shippingService.updateShippingStatus(shippingUpdateDTO);
    }

    @PostMapping("/requestRefund")
    public RefundAndReturnResponseDTO raiseRefundReq(@RequestBody RaiseRefundRequestDto refundRequestDto){
        return refundService.requestRefund(refundRequestDto);
    }

    @GetMapping("/genInvoice/{orderid}")
    public Invoice generateInvoice(@PathVariable String orderid){
        return invoiceService.generateInvoice(orderid);
    }


    @PostMapping("/updatDelivery")
    public String updateDelivery(@RequestBody  DeliveryUpdateDTO deliveryUpdateDTO){
//        orderService.getOrder(deliveryUpdateDTO.getOrderId()).setOrderStatus("DELIVERED");
        Order order = orderService.getOrder(deliveryUpdateDTO.getOrderId());
        if(deliveryUpdateDTO.getPaymentStatus().equalsIgnoreCase("success")){
            order.setOrderStatus("DELIVERED");
            order.setPaymentStatus("SUCCESS");
        }
        orderService.saveOrder(order);
//        deliveryService.updateOrder(deliveryUpdateDTO);
        return shippingService.updateDeliveryStatus(deliveryUpdateDTO);
    }
    @PostMapping("/updateReturn")
    public Refund updateReturn(@RequestBody  ReturnUpdate returnUpdate){
        if(returnUpdate.isPicked()){
            returnService.updateReturnSuccess(returnUpdate.getOrderId());
            Refund refund = refundService.getRefundsByOrderId(returnUpdate.getOrderId());
            return refundService.completeRefund(refund.getRefundId());
        }
        returnService.updateReturnFailed(returnUpdate.getOrderId());
        Refund refund = refundService.getRefundsByOrderId(returnUpdate.getOrderId());
        return refundService.rejectRefund(refund.getRefundId(),"Product Damaged.");
//        return null;
    }

    @PostMapping("/updateStock")
    public StockLog insertStock(@RequestBody StockLogModificationDTO stockLogModificationDTO){
        return stockLogService.modifyStock(stockLogModificationDTO);
    }


}

