package com.ECommerceApp.ServiceInterface;

import com.ECommerceApp.DTO.ReturnAndExchange.RaiseRefundRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.RefundAndReturnResponse;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Delivery.ShippingDetails;
import com.ECommerceApp.Model.Order.OrderItem;
import com.ECommerceApp.Model.RefundAndExchange.Refund;

import java.util.Date;
import java.util.List;

public interface IReturnService {

    ShippingDetails updateShippingStatusForRefundAndReturn(String orderId);

    DeliveryPerson assignReturnProductToDeliveryPerson(ShippingDetails shippingDetails, String reason);

    RefundAndReturnResponse getRefundAndReturnResponce(DeliveryPerson deliveryPerson, Refund refund);

    Date getExpectedDate(Date date);

    void updateReturnSuccess(String orderId);

    void updateReturnFailed(String orderId);

    void updateStockLogAfterReturn(String orderId);

    void updateStockLogAfterOrderCancellation(String orderId);

    void updateOrderItemsForReturn(List<OrderItem> orderItems, RaiseRefundRequest refundRequestDto);
}

