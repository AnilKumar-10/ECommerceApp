package com.ECommerceApp.ServiceInterface.Order;

import com.ECommerceApp.DTO.ReturnAndExchange.RaiseRefundRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.RefundAndReturnResponse;
import com.ECommerceApp.DTO.ReturnAndExchange.ReturnUpdateRequest;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Model.Order.OrderItem;
import com.ECommerceApp.Model.RefundAndExchange.Refund;

import java.util.Date;
import java.util.List;

public interface IRefundService {

    RefundAndReturnResponse requestRefund(RaiseRefundRequest refundRequestDto);

    Refund approveRefund(String refundId, String adminId);

    Refund rejectRefund(ReturnUpdateRequest returnUpdate);

    Refund completeRefund(ReturnUpdateRequest returnUpdate);

    Refund getRefundById(String refundId);

    List<Refund> getRefundsByUserId(String userId);

    List<Refund> getRefundsByStatus(String status);

    Refund getRefundsByOrderId(String orderId);

    void deleteRefund(String refundId);

    void checkProductReturnable(String productId, String shippingId);

    boolean isReturnAvailable(Date deliveredDate, int returnBeforeDays);

    Date getDeliveredTimestamp(String shippingId);

    Order cancelOrder(String orderId, String cancelReason);

    Refund refundOverOrderCancellation(Order order);

    Refund saveRefund(Refund refund);

    double processRefundForItem(Order order, OrderItem orderItem);
}
