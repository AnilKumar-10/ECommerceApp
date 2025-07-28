package com.ECommerceApp.Controller;


import com.ECommerceApp.ServiceImplementation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private TaxRuleService taxRuleService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductSearchService productSearchService;


    @GetMapping("/home")
    public String getHome(){
        return "welcome";
    }
// ========= PRODUCT  ==============
//    @PostMapping("/insertProduct")
//    public Product insertProduct(@RequestBody  ProductRequest product){
//        return productService.createProduct(product);
//    }
//
//    @PostMapping("/insertProducts")
//    public String  insertProducts(@RequestBody  List<ProductRequest> product){
//        return productService.createProductList(product);
//    }
//
//
//    @GetMapping("/getprd/{id}")
//    public Product getProduct(@PathVariable String id){
//        return productService.getProductById(id);
//    }
//
//    @GetMapping("/get")
//    public List<Product> get(){
//        return productService.getAllProducts();
//    }
//
//
//    // =========  USER   =================
//    @PostMapping("/insertUser")
//    public Users insertUser(@RequestBody Users users){
//        return  userService.registerUser(users);
//    }
//
//    @PostMapping("/insertUsers")
//    public String insertUsers(@RequestBody List<Users> users){
//        return  userService.registerUsers(users);
//    }
//
//    @GetMapping("/users")
//    public List<Users> getAllUsers(){
//       return userService.getAllUsers();
//    }

//
//    // ==========  ADDRESS   ================
//    @PostMapping("/insertAddress")
//    public Address insertUserAddress(@RequestBody Address address){
//        return  addressService.createAddress(address);
//    }
//
//    @PostMapping("/insertAddresses")
//    public String  insertUsersAddress(@RequestBody List<Address> address){
//            return addressService.createAddresses(address);
//    }
//
//    @GetMapping("/getAddress/{id}")
//    public List<Address> getUserAddress(@PathVariable String id){
//        return addressService.getAddressesByUserId(id);
//    }
//
//    @GetMapping("/allAddress")
//    public List<Address> getAddressess(){
//        return addressService.getAllAddressess();
//    }
//
//
//    // ============  CART   ====================
//    @PostMapping("/insertCart")
//    public Cart insertCart(@RequestBody CartItem items){
//        items.setPrice(productService.getProductPrice(items.getProductId())* items.getQuantity());
//        System.out.println(items);
//        return cartService.addItemToCart("USER1006",items);
//    }
//
//    @GetMapping("/getcart/{id}")
//    public Cart getCart(@PathVariable String id){
//        return cartService.getCartByBuyerId(id);
//    }
//
//    @PostMapping("/clearCart/{userId}")
//    public Cart clearCart(@PathVariable String userId){
//        return cartService.clearCart(userId);
//    }
//
//
//    // ============  REVIEW  ===================
//    @PostMapping("/postReview")
//    public Review postReview(@RequestBody Review review){
//        return reviewService.addReview(review);
//    }
//
//    @GetMapping("/getReview/{id}")
//    public List<Review> getProductReview(@PathVariable String id){
//        return reviewService.getReviewByProductId(id);
//    }
//
//
//    // ========== WISH LIST =====================
//    @PostMapping("/addWish")
//    public Wishlist insertWish(@RequestBody Map<String,String > pid ){
//        return wishListService.addToWishlist("USER1002",pid.get("id"));
//    }
//
//    @GetMapping("/getWish")
//    public Wishlist getWish(){
//        return  wishListService.getWishlistByBuyerId("USER1002");
//    }
//
//// ================  COUPON ===============
//    @PostMapping("/insertCoupon")
//    public Coupon insertCoupon(@RequestBody Coupon coupon){
//        return couponService.createCoupon(coupon);
//    }
//
//
//    // ========== ORDER  ===================
//    @PostMapping("/placeOrder")
//    public Order placeOrder(@RequestBody OrderDto orderDto){
//        return orderService.createOrder(orderDto);
//    }
//
//    @PostMapping("/initUpiPay")
//    public Payment initiatePaymentDto(@RequestBody InitiatePaymentDto initiatePaymentDto){
//        return paymentService.initiatePayment(initiatePaymentDto);
//    }
//
//    @PostMapping("/pay")
//    public Payment pay(@RequestBody PaymentDto paymentDto){
//        return paymentDto.getStatus().equalsIgnoreCase("Success")?paymentService.confirmUPIPayment(paymentDto):paymentService.failPayment(paymentDto);
//    }
//
//    @GetMapping("/getOrder/{id}")
//    public Order getOrder(@PathVariable String id){
//        return orderService.getOrder(id);
//    }
//
//
//    // ================ DELIVERY  ===============
//    @PostMapping("/insertDelivery")
//    public DeliveryPerson insertDelivery(@RequestBody DeliveryPerson deliveryPerson){
//        return  deliveryService.register(deliveryPerson);
//    }
//
//    @PostMapping("/insertDeliverys")
//    public String  insertDeliveryPersons(@RequestBody List<DeliveryPerson> deliveryPerson){
//        return  deliveryService.registerPersons(deliveryPerson);
//    }
//
//    // ========== SHIPPING  ==========
//    @GetMapping("/getShipping/{id}")
//    public ShippingDetails getShipping(@PathVariable String id){
//        return shippingService.getShippingByOrderId(id);
//    }
//
//    @PostMapping("/updateShipping")
//    public ShippingDetails updateShip(@RequestBody  ShippingUpdateDTO shippingUpdateDTO){
//        return shippingService.updateShippingStatus(shippingUpdateDTO);
//    }
//
//
//    // ============= RETURN AND REFUND =============
//    @PostMapping("/requestRefund")
//    public RefundAndReturnResponseDTO raiseRefundReq(@RequestBody RaiseRefundRequestDto refundRequestDto){
//        return refundService.requestRefund(refundRequestDto);
//    }
//
//    @GetMapping("/genInvoice/{orderid}")
//    public Invoice generateInvoice(@PathVariable String orderid){
//        return invoiceService.generateInvoice(orderid);
//    }
//
//    @PostMapping("/initCODPay")
//    public Payment initiateCODPay(@RequestBody InitiatePaymentDto initiatePaymentDto){
//        return paymentService.initiatePayment(initiatePaymentDto);
//    }
//
//    // ============== OUT FOR DELIVERY  =============
//    @PostMapping("/updateDelivery")
//    public String updateDelivery(@RequestBody  DeliveryUpdateDTO deliveryUpdateDTO){
//        if(orderService.getOrder(deliveryUpdateDTO.getOrderId()).getPaymentMethod().equalsIgnoreCase("COD")){
//            System.out.println("inside the if of update: "+deliveryUpdateDTO);
//            PaymentDto paymentDto = new PaymentDto();
//            paymentDto.setPaymentId(deliveryUpdateDTO.getPaymentId());
//            paymentDto.setTransactionId(orderService.generateTransactionIdForCOD());
//            paymentDto.setStatus("SUCCESS");
//            paymentService.confirmCODPayment(paymentDto); // updating the payment success details
//            orderService.updateCODPaymentStatus(deliveryUpdateDTO);// updating the order payment status
//        }
//        return shippingService.updateDeliveryStatus(deliveryUpdateDTO);
//    }
//    @PostMapping("/updateReturn")
//    public Refund updateReturn(@RequestBody  ReturnUpdate returnUpdate){
//        if(returnUpdate.isPicked()){
//            returnService.updateReturnSuccess(returnUpdate.getOrderId());
//            DeliveryPerson deliveryPerson = deliveryService.getDeliveryPerson(returnUpdate.getDeliveryPersonId());
//            deliveryPerson.getToReturnItems();
//            Refund refund = refundService.getRefundsByOrderId(returnUpdate.getOrderId());
//            return refundService.completeRefund(refund.getRefundId());
//        }
//        returnService.updateReturnFailed(returnUpdate.getOrderId());
//        Refund refund = refundService.getRefundsByOrderId(returnUpdate.getOrderId());
//        return refundService.rejectRefund(refund.getRefundId(),"Product Damaged.");
//    }
//
//
//    // ========== STOCK ================
//    @PostMapping("/updateStock")
//    public StockLog insertStock(@RequestBody StockLogModificationDTO stockLogModificationDTO){
//        return stockLogService.modifyStock(stockLogModificationDTO);
//    }
//// ====================================
//    // tax controller
//    @PostMapping("/createTax")
//    public String  createTax(@RequestBody List<TaxRule> rule){
//        return taxRuleService.createMultiTaxRules(rule);
//    }
//
//    // category controller
//    @PostMapping("/insertCategory")
//    public String  inserCategory(@RequestBody List<Category> categories){
//        return categoryService.createCategoryList(categories);
//    }
//
//
//    // ======  SEARCH TEST  ================
//    @GetMapping("/product/{name}")
//    public List<ProductSearchDto> getProductByCategoryName(@PathVariable String name){
//        List<Product> products = productSearchService.getProductsByCategoryName(name);
//        System.out.println("products are: "+products);
//        List<ProductSearchDto> productSearchDtos  = new ArrayList<>();
//        for(Product product : products){
//            ProductSearchDto productSearchDto = new ProductSearchDto();
//            BeanUtils.copyProperties(product , productSearchDto);
//            productSearchDtos.add(productSearchDto);
//        }
//        return productSearchDtos;
//    }
//
//
//    @GetMapping("/search")
//    public List<ProductSearchDto> searchProductsByCategoryNames(
//            @RequestParam List<String> categories,
//            @RequestParam(name = "sortOrder", required = false, defaultValue = "asc") String sortOrder,
//            @RequestParam(name = "sortBy", required = false, defaultValue = "rating") String sortBy,HttpServletRequest httpServletRequest) {
//        System.out.println("http url: "+httpServletRequest.getQueryString());
//        System.out.println("sort: "+sortOrder+"  soryby: "+sortBy);
//        List<ProductSearchDto> productSearchDtos = new ArrayList<>();
//        List<Product> products = productSearchService.searchProductsByCategoryNames(categories);
//
//        for (Product product : products) {
//            ProductSearchDto dto = new ProductSearchDto();
//            BeanUtils.copyProperties(product, dto);
//            productSearchDtos.add(dto);
//        }
//
//        Comparator<ProductSearchDto> comparator;
//        if ("rating".equalsIgnoreCase(sortBy)) {
//            comparator = Comparator.comparingDouble(ProductSearchDto::getRating);
//        } else {
//            comparator = Comparator.comparingDouble(ProductSearchDto::getPrice);
//        }
//
//        if ("desc".equalsIgnoreCase(sortOrder)) {
//            comparator = comparator.reversed();
//        }
//
//        productSearchDtos.sort(comparator);
//
//        return productSearchDtos;
//    }
//
//
//
//
//

// start from testing the emails services
// start from cart service
// we have to provide  the sizess for that add some sizes to the watches product to over come that error.

}

