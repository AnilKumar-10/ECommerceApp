package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.DeliveryUpdateDTO;
import com.ECommerceApp.DTO.OrderDto;
import com.ECommerceApp.DTO.StockLogModificationDTO;
import com.ECommerceApp.Exceptions.OrderNotFoundException;
import com.ECommerceApp.Model.*;
import com.ECommerceApp.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private SequenceGeneratorService sequenceService;
    @Autowired
    private ShippingService shippingService;
    @Autowired
    private ProductService productService;
    @Autowired
    private StockLogService stockLogService;

    public Order createOrder(OrderDto orderDto){
        List<OrderItem> orderItem = cartService.checkOutForOrder(orderDto.getProductIds(),orderDto.getUserId());
        for(OrderItem item : orderItem){
            Product product = productService.getProductById(item.getProductId());
            if(!product.isAvailable()){
                throw new RuntimeException(product.getName()+" is Out of Stock");
            }
        }
        Order order = new Order();
        System.out.println("orderDTO:  "+orderDto);
        System.out.println("orderItems:  "+orderItem);
        long nextId = sequenceService.getNextSequence("orderId");
        order.setId(String.valueOf(nextId)); // If id is String
        order.setOrderItems(orderItem);
        order.setBuyerId(orderDto.getUserId());
        String addressId = getAddress(orderDto.getUserId(),orderDto.getAddressType());
        order.setAddressId(addressId);
        order.setCouponId(orderDto.getCoupon());
        order.setOrderDate(new Date());
        double value = getTotalAmount(orderDto);
        System.out.println("value: "+value);
        double amount = Math.round(value * 100.0) / 100.0;
        System.out.println("amount in order servie: "+amount);
        order.setTotalAmount(amount);
        double discountAmount = Math.round(getCouponDiscount(orderDto,amount)*100.0)/100.0;
        order.setDiscount(discountAmount);
        double tax = getTax(addressId);
        order.setTax(tax);
        double finalAmount = amount - discountAmount + tax;
        order.setFinalAmount(Math.round(finalAmount * 100.0) / 100.0);
        order.setPaymentMethod(orderDto.getPayMode());
        order.setOrderDate(new Date());
        order.setOrderStatus(orderDto.getPayMode().equalsIgnoreCase("COD")?"PLACED":"PENDING");
        order.setPaymentStatus("PENDING"); // pending until the payment is successful
        Order order1 = orderRepository.save(order);
        if(orderDto.getPayMode().equalsIgnoreCase("COD")){ // if the paymode is UPI then the shipping details must be generated after the payment.
            shippingService.createShippingDetails(order1);
            updateStockLogAfterOrderConfirmed(order1.getId()); // this will update the product stock.
            cartService.removeOrderedItemsFromCart(order1); // here the order is confirmed without the payment.
        }
        return order1; // flow goes to the initiating payment is the paymode is upi
    }

    private double getTax(String addressId) {
        return  0.0;
    }

    public String getAddress(String userId,String type){
        List<Address> address = addressService.getAddressesByUserId(userId);
        String addressId = "";
        for(Address adrs : address){
            if(adrs.getType().equals(type)){
                addressId = adrs.getId();
            }
        }
        return addressId;
    }

    public double getCouponDiscount(OrderDto orderDto,double amount){
        Coupon coupon = couponService.validateCoupon(orderDto.getCoupon(),orderDto.getUserId(),amount);
        return couponService.getDiscountAmount(coupon,amount);
    }


    public double getTotalAmount(OrderDto orderDto){
        List<OrderItem> orderItem = cartService.checkOutForOrder(orderDto.getProductIds(),orderDto.getUserId());
        System.out.println(orderItem);
        double amount=0.0;
        for(OrderItem item : orderItem){
            amount += item.getPrice();
        }
        System.out.println("amount is method: "+amount);
        return amount;
    }

    // update the order and payment status after the completion of payment.
    public void markOrderAsPaid(String orderId, String paymentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        order.setPaymentStatus("SUCCESS");
        order.setOrderStatus("CONFIRMED");
        order.setPaymentId(paymentId);
        Order order1 = orderRepository.save(order);
        shippingService.createShippingDetails(order1); // after successful payment we generate the shipping details.
        updateStockLogAfterOrderConfirmed(orderId); // after the order is confirmed the stock details get updated.
        cartService.removeOrderedItemsFromCart(order1);
    }

    public void markOrderAsPaymentFailed(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        order.setPaymentStatus("FAILED");
        order.setOrderStatus("CANCELLED");
        order.setCancelled(true);
        order.setCancelReason("Payment failed");
        order.setCancellationTime(new Date());
        orderRepository.save(order);
    }


    // update the stock details after the order is confirmed
    private void updateStockLogAfterOrderConfirmed(String orderId) {
        Order order = getOrder(orderId);
        List<OrderItem> orderedProducts = order.getOrderItems();
        for(OrderItem orderItem : orderedProducts){
            StockLogModificationDTO stockLogModificationDTO = new StockLogModificationDTO();
            stockLogModificationDTO.setAction("SOLD");
            stockLogModificationDTO.setModifiedAt(new Date());
            stockLogModificationDTO.setQuantityChanged(orderItem.getQuantity());
            stockLogModificationDTO.setSellerId(productService.getProductById(orderItem.getProductId()).getSellerId());
            stockLogModificationDTO.setProductId(orderItem.getProductId());
            stockLogService.modifyStock(stockLogModificationDTO);
        }
    }


    // update the stock details after the order is returned
//    private void updateStockLogAfterOrderReturned(String orderId) {
//        Order order = getOrder(orderId);
//        List<OrderItem> orderedProducts = order.getOrderItems();
//        for(OrderItem orderItem : orderedProducts){
//            StockLogModificationDTO stockLogModificationDTO = new StockLogModificationDTO();
//            stockLogModificationDTO.setAction("RETURN");
//            stockLogModificationDTO.setModifiedAt(new Date());
//            stockLogModificationDTO.setQuantityChanged(orderItem.getQuantity());
//            stockLogModificationDTO.setSellerId(productService.getProductById(orderItem.getProductId()).getSellerId());
//            stockLogModificationDTO.setProductId(orderItem.getProductId());
//            stockLogService.modifyStock(stockLogModificationDTO);
//        }
//    }

    public Order saveOrder(Order order){
        return orderRepository.save(order);
    }

    public Order getOrder(String id){
        return orderRepository.findById(id).get();
    }

    public void updateCODPaymentStatus(DeliveryUpdateDTO deliveryUpdateDTO){
        Order order = getOrder(deliveryUpdateDTO.getOrderId());
        if(deliveryUpdateDTO.getPaymentStatus().equalsIgnoreCase("success")){
            order.setOrderStatus("DELIVERED");
            order.setPaymentStatus("SUCCESS");
            order.setPaymentId(deliveryUpdateDTO.getPaymentId());
        }
        saveOrder(order);
    }

    // generate the random transaction id's for COD orders.
    public String generateTransactionIdForCOD() {
        return "TRNSCN-" + UUID.randomUUID().toString().toUpperCase().replaceAll("-", "").substring(0, 10);
    }


}
