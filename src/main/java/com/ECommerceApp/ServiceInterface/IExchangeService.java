package com.ECommerceApp.ServiceInterface;

import com.ECommerceApp.DTO.ReturnAndExchange.*;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Model.Order.OrderItem;
import com.ECommerceApp.Model.RefundAndExchange.Refund;

public interface IExchangeService {

    ExchangeResponse exchangeRequest(ProductExchangeRequest productExchangeDto);

    ExchangeDetails updateExchangeDetails(Order order, ProductExchangeRequest productExchangeDto, double newPrice, double oldPrice);

    OrderItem createNewOrderItem(Order order, ProductExchangeRequest productExchangeDto);

    double getTaxForNewProduct(String addressId, String productId);

    double calculateNewFinalAmount(double oldPrice, double newPrice, double finalOrderAmount, double totalOriginalPrice, double totalDiscount);

    Refund initiateRefundForExchange(Order order);

    void processExchangeAfterUpiPayDone(String orderId, String paymentId);

    void markExchangeCodPaymentSuccess(ExchangeUpdateRequest exchangeUpdateRequest);

    void updateNewProductStockToReplace(OrderItem item);

    DeliveryPerson assignDeliveryForExchange(Order order);

    ProductExchangeInfo getExchangeInformation(String orderId);

    Order updateExchangeSuccess(String orderId, String deliveryPersonId);

    void updateStockAfterExchangeSuccess(String orderId);

    void completeRefundAfterExchangeSuccess(String refundId);

    void exchangeUpdate(ExchangeUpdateRequest exchangeUpdateRequest);
}

