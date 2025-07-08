package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.OrderDto;
import com.ECommerceApp.Exceptions.OrderNotFoundException;
import com.ECommerceApp.Model.Address;
import com.ECommerceApp.Model.Coupon;
import com.ECommerceApp.Model.Order;
import com.ECommerceApp.Model.OrderItem;
import com.ECommerceApp.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

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

    public Order createOrder(OrderDto orderDto){
        Order order = new Order();
        System.out.println("orderDTO:  "+orderDto);
        List<OrderItem> orderItem = cartService.checkOutForOrder(orderDto.getProductIds(),orderDto.getUserId());
        System.out.println("orderItems:  "+orderItem);
        long nextId = sequenceService.getNextSequence("orderId");
        order.setId(String.valueOf(nextId)); // If id is String
        order.setOrderItems(orderItem);
        order.setBuyerId(orderDto.getUserId());
        order.setAddressId(getAddress(orderDto.getUserId(),orderDto.getAddressType()));
        order.setCouponId(orderDto.getCoupon());
        order.setOrderDate(new Date());
        double value = getTotalAmount(orderDto);
        double amount = Math.round(value * 100.0) / 100.0;
        order.setTotalAmount(amount);
        double discountAmount = Math.round(getCouponDiscount(orderDto,amount)*100.0)/100.0;
        order.setDiscount(discountAmount);
        double finalAmount = amount-discountAmount;
        order.setFinalAmount(finalAmount);
        order.setPaymentMethod(orderDto.getPayMode());
        order.setOrderDate(new Date());
        order.setOrderStatus(orderDto.getPayMode().equalsIgnoreCase("COD")?"PLACED":"PENDING");
//        order.setShippingId(shippingService.createShippingDetails());
        order.setPaymentStatus("PENDING"); // pending until the payment is successful
       // the remaining fields like status are updated after the payment
        Order order1 = orderRepository.save(order);
        order1.setShippingId(shippingService.createShippingDetails(order1).getId());
        return order1; // flow goes to the initiating payment
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
        double amount=0.0;
        for(OrderItem item : orderItem){
            amount += item.getPrice();
        }
        return amount;
    }


    public void markOrderAsPaid(String orderId, String paymentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        order.setPaymentStatus("SUCCESS");
        order.setOrderStatus("CONFIRMED");
        order.setPaymentId(paymentId);
        orderRepository.save(order);
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

    public Order saveOrder(Order order){
        return orderRepository.save(order);
    }

    public Order getOrder(String id){
        return orderRepository.findById(id).get();
    }
}
