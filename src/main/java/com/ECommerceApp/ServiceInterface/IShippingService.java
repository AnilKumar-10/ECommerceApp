package com.ECommerceApp.ServiceInterface;

import com.ECommerceApp.DTO.Delivery.DeliveryUpdate;
import com.ECommerceApp.DTO.Order.ShippingUpdateRequest;
import com.ECommerceApp.Model.Delivery.ShippingDetails;
import com.ECommerceApp.Model.Order.Order;

import java.util.List;

public interface IShippingService {

    ShippingDetails createShippingDetails(Order order);

    ShippingDetails updateShippingStatus(ShippingUpdateRequest shippingUpdateDTO);

    ShippingDetails getShippingByOrderId(String orderId);

    List<ShippingDetails> getByDeliveryPersonId(String deliveryPersonId);

    String updateDeliveryStatus(DeliveryUpdate deliveryUpdateDTO);

    ShippingDetails getByShippingId(String shippingId);

    String generateTrackingId();

    Order updateOrderItemsDeliveredStatus(ShippingUpdateRequest shippingUpdateDTO);

    String getCourierName();
}

