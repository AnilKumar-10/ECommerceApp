package com.ECommerceApp.ServiceImplementation.Order;

import com.ECommerceApp.DTO.Delivery.DeliveryUpdate;
import com.ECommerceApp.DTO.Order.OrderStatusCount;
import com.ECommerceApp.DTO.Order.PlaceOrderRequest;
import com.ECommerceApp.DTO.Product.StockLogModificationRequest;
import com.ECommerceApp.Exceptions.Order.OrderNotFoundException;
import com.ECommerceApp.Exceptions.Product.ProductOutOfStockException;
import com.ECommerceApp.Model.Delivery.ShippingDetails;
import com.ECommerceApp.Model.Order.*;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.Model.Product.StockLogModification;
import com.ECommerceApp.Model.User.Address;
import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.Repository.OrderRepository;
import com.ECommerceApp.ServiceInterface.Order.IOrderService;
import com.ECommerceApp.ServiceInterface.Order.ICouponService;
import com.ECommerceApp.ServiceInterface.Order.IShippingService;
import com.ECommerceApp.ServiceInterface.Order.ITaxRuleService;
import com.ECommerceApp.ServiceInterface.Product.ICategoryService;
import com.ECommerceApp.ServiceInterface.Product.IProductService;
import com.ECommerceApp.ServiceInterface.Product.IStockLogService;
import com.ECommerceApp.ServiceInterface.User.IAddressService;
import com.ECommerceApp.ServiceInterface.User.ICartService;
import com.ECommerceApp.ServiceInterface.User.IEmailService;
import com.ECommerceApp.ServiceInterface.User.UserServiceInterface;
import com.ECommerceApp.Util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
public class OrderService implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ICartService cartService;
    @Autowired
    private IAddressService addressService;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private SequenceGeneratorService sequenceService;
    @Autowired
    private IShippingService shippingService;
    @Autowired
    private IProductService productService;
    @Autowired
    private IStockLogService stockLogService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ITaxRuleService taxRuleService;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private UserServiceInterface userService;


    public Order createOrder(PlaceOrderRequest orderDto){
        log.info("Creating the order for : {}", orderDto.getUserId());
        String userId =new SecurityUtils().getCurrentUserId();
        Users users = userService.getUserById(userId);
        List<OrderItem> orderItem = cartService.checkOutForOrder(orderDto.getProductIds(),userId);
        for(OrderItem item : orderItem){
            Product product = productService.getProductById(item.getProductId());
            if(!product.isAvailable()){
                log.warn("The product to order is out of stock..");
                throw new ProductOutOfStockException(product.getName()+" is Out of Stock");
            }
        }
        Order order = new Order();
        long nextId = sequenceService.getNextSequence("orderId");
        order.setId(String.valueOf(nextId)); // If id is String
        order.setOrderItems(orderItem);
        order.setBuyerId(userId);
        String addressId = getAddress(userId,orderDto.getAddressType());
        order.setAddressId(addressId);
        order.setOrderDate(new Date());
        double amount = getTotalAmount(orderDto);
        order.setTotalAmount(amount);
        double discountAmount =0;
        if(orderDto.getCoupon()!=null){
            order.setCouponId(orderDto.getCoupon());
            discountAmount = getCouponDiscount(orderDto,amount);
        }
        order.setDiscount(discountAmount);
        double tax = calculateTaxForOrder(order,addressId);
        order.setTax(tax);
        double finalAmount = amount - discountAmount + tax;
        order.setFinalAmount(Math.round(finalAmount * 100.0) / 100.0);
        order.setPaymentMethod(orderDto.getPayMode());
        order.setOrderDate(new Date());
        order.setUpiId(users.getUpiId());
        order.setOrderStatus(orderDto.getPayMode() == Payment.PaymentMethod.COD ? Order.OrderStatus.PLACED: Order.OrderStatus.PENDING);
        order.setPaymentStatus(Payment.PaymentStatus.PENDING); // pending until the payment is successful
        Order order1 = saveOrder(order);
        if(orderDto.getPayMode() == Payment.PaymentMethod.COD){
            // if the pay mode is UPI then the shipping details must be generated after the payment.
            log.info("The payment mode is COD so process the shipping.");
            ShippingDetails shippingDetails = shippingService.createShippingDetails(order1);
            updateStockLogAfterOrderConfirmed(order1.getId()); // this will update the product stock.
            cartService.removeOrderedItemsFromCart(order1); // here the order is confirmed without the payment.
            emailService.sendOrderConfirmationEmail("iamanil3121@gmail.com",userService.getUserName(userId), order1, shippingDetails);
        }
        else{
            log.info("The payment mode is UPI/ONLINE so shipping is processes after the payment.");
        }
        return order1; // flow goes to the initiating payment is the pay mode is upi
    }


    public String getAddress(String userId,String type){
        List<Address> addresses = addressService.getAddressesByUserId(userId);
        String addressId = "";
        for(Address address : addresses){
            if(address.getType().equals(type)){
                addressId = address.getId();
            }
        }
        return addressId;
    }

    public double getCouponDiscount(PlaceOrderRequest orderDto, double amount){
        couponService.recordCouponUsage(orderDto.getCoupon(),orderDto.getUserId());// this is used to track the no of time the coupon is used by user
        Coupon coupon = couponService.validateCoupon(orderDto.getCoupon(),orderDto.getUserId(),amount);
        double discount = couponService.getDiscountAmount(coupon,amount);
        log.info("The calculated discount is: {}", discount);
        return Math.round(discount * 100.0)/100.0;
    }


    public double getTotalAmount(PlaceOrderRequest orderDto){
        List<OrderItem> orderItem = cartService.checkOutForOrder(orderDto.getProductIds(),orderDto.getUserId());
        System.out.println(orderItem);
        double amount=0.0;
        for(OrderItem item : orderItem){
            amount += item.getPrice();
        }
        log.info("The total amount of product excluding the tax is: {}", amount);
        return Math.round(amount * 100.0) / 100.0;
    }

    // update the order and payment status after the completion of payment.
    public void markOrderAsPaid(String orderId, String paymentId) {
        String userId = new SecurityUtils().getCurrentUserId();
        log.info("updating UPI payment success in order details");
        Order order = getOrder(orderId);
        order.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        order.setOrderStatus(Order.OrderStatus.PLACED);
        order.setPaymentId(paymentId);
        Order order1 = saveOrder(order);
        ShippingDetails shippingDetails = shippingService.createShippingDetails(order1); // after successful payment we generate the shipping details.
        updateStockLogAfterOrderConfirmed(orderId); // after the order is confirmed the stock details get updated.
        cartService.removeOrderedItemsFromCart(order1); // this will remove the ordered items from the cart.
        emailService.sendOrderConfirmationEmail("iamanil3121@gmail.com", userService.getUserName(userId), order1,shippingDetails);

    }

    public void markOrderAsPaymentFailed(String orderId) {
        log.info("updating UPI payment failed in order details");
        Order order = getOrder(orderId);
        order.setPaymentStatus(Payment.PaymentStatus.FAILED);
        order.setOrderStatus(Order.OrderStatus.CANCELLED);
        order.setCancelled(true);
        order.setCancelReason("Payment failed");
        order.setCancellationTime(new Date());
        saveOrder(order);
    }


    // update the stock details after the order is confirmed
    private void updateStockLogAfterOrderConfirmed(String orderId) {
        log.info("updating the stock of the ordered products as sold out");
        Order order = getOrder(orderId);
        List<OrderItem> orderedProducts = order.getOrderItems();
        for(OrderItem orderItem : orderedProducts){
            StockLogModificationRequest stockLogModificationDTO = new StockLogModificationRequest();
            stockLogModificationDTO.setAction(StockLogModification.ActionType.SOLD);
            stockLogModificationDTO.setModifiedAt(new Date());
            stockLogModificationDTO.setQuantityChanged(orderItem.getQuantity());
            stockLogModificationDTO.setSellerId(productService.getProductById(orderItem.getProductId()).getSellerId());
            stockLogModificationDTO.setProductId(orderItem.getProductId());
            stockLogService.modifyStock(stockLogModificationDTO);
        }
    }


    public Order saveOrder(Order order){
        return orderRepository.save(order);
    }

    public Order getOrder(String id){
        log.info("getting the order with id: {}", id);
        return orderRepository.findById(id).orElseThrow(()-> new OrderNotFoundException("There is no order found with id: "+id));
    }

    public void updateCODPaymentStatus(DeliveryUpdate deliveryUpdateDTO){
        log.info("updating COD payment success in order details");
        Order order = getOrder(deliveryUpdateDTO.getOrderId());
        if(deliveryUpdateDTO.getPaymentStatus() == Payment.PaymentStatus.SUCCESS){
            order.setOrderStatus(Order.OrderStatus.DELIVERED);
            order.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
            order.setPaymentId(deliveryUpdateDTO.getPaymentId());
        }
        saveOrder(order);
    }

    // generate the random transaction id's for COD orders.
    public String generateTransactionIdForCOD() {
        return "TRNSCN-" + UUID.randomUUID().toString().toUpperCase().replaceAll("-", "").substring(0, 10);
    }

    // this will calculate the tax of every product and return the whole tax applicable for the order.
    public double calculateTaxForOrder(Order order,String addressId){
        log.info("calculating the tax for each product based on the category they belong.");
        List<OrderItem> orderItems = order.getOrderItems();
        double totalTax = 0.0;
        String shippingState = addressService.getAddressById(addressId).getState();

        for (OrderItem item : orderItems) {
            Product product = productService.getProductById(item.getProductId());
            String rootCategoryId = categoryService.getRootCategoryId(product.getCategoryIds());
            double taxRate = taxRuleService.getApplicableTaxRate(rootCategoryId, shippingState);

            double tax = (item.getPrice() * taxRate) / 100;
            item.setTax(tax);

            totalTax += tax;
        }
        return Math.round(totalTax * 100.0)/100.0;
    }

    public List<Order> getAllOrderByUserId(String userId) {
        return orderRepository.findAllByBuyerId(userId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getAllPendingOrders() {
        return orderRepository.findAllPendingOrders();
    }


    public List<OrderStatusCount> getOrderCountByStatus() {
        return orderRepository.countOrdersByStatus();
    }


}
