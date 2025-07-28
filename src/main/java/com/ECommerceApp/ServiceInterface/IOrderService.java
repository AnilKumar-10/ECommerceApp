package com.ECommerceApp.ServiceInterface;

import com.ECommerceApp.DTO.Delivery.DeliveryUpdate;
import com.ECommerceApp.DTO.Order.PlaceOrderRequest;
import com.ECommerceApp.Model.Order.Order;

import java.util.List;

public interface IOrderService {

    Order createOrder(PlaceOrderRequest orderDto);

    String getAddress(String userId, String type);

    double getCouponDiscount(PlaceOrderRequest orderDto, double amount);

    double getTotalAmount(PlaceOrderRequest orderDto);

    void markOrderAsPaid(String orderId, String paymentId);

    void markOrderAsPaymentFailed(String orderId);

    Order saveOrder(Order order);

    Order getOrder(String id);

    void updateCODPaymentStatus(DeliveryUpdate deliveryUpdateDTO);

    String generateTransactionIdForCOD();

    double calculateTaxForOrder(Order order, String addressId);

    List<Order> getAllOrderByUserId(String userId);

    List<Order> getAllOrders();

    List<Order> getAllPendingOrders();
}
