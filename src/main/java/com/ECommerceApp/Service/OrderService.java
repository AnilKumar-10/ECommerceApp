package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.OrderDto;
import com.ECommerceApp.Model.Address;
import com.ECommerceApp.Model.Coupon;
import com.ECommerceApp.Model.Order;
import com.ECommerceApp.Model.OrderItem;
import com.ECommerceApp.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Order createOrder(OrderDto orderDto){
        Order order = new Order();
        List<OrderItem> orderItem = cartService.checkOutForOrder(orderDto.getProductId(),orderDto.getUserId());
        order.setOrderItems(orderItem);
        order.setBuyerId(orderDto.getUserId());
        order.setAddressId(getAddress(orderDto.getUserId(),orderDto.getAdrstype()));
        order.setCouponId(orderDto.getCoupon());

        return null;
    }

    public String getAddress(String userId,String type){
        List<Address> address = addressService.getAddressesByUserId(userId);
        String addressId="";
        for(Address adrs : address){
            if(adrs.getType().equals(type)){
                addressId = adrs.getId();
            }
        }
        return addressId;
    }

    public String getCoupon(OrderDto orderDto,double amount){
        Coupon coupon = couponService.validateCoupon(orderDto.getCoupon(),orderDto.getUserId(),amount);
        return "";

    }


    public double totalAmount(OrderDto)

}
