package com.ECommerceApp.ServiceInterface.Delivery;

import com.ECommerceApp.DTO.Delivery.DeliveryPersonResponse;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Order.Order;

public interface IDeliveryService {

    DeliveryPerson assignDeliveryPerson(String deliveryAddress);

    DeliveryPerson getDeliveryPerson(String id);

    DeliveryPerson assignProductsToDelivery(String deliveryPersonId, Order order);

    DeliveryPerson updateDeliveryPerson(DeliveryPerson deliveryPerson);

    void removeDeliveredOrderFromToDeliveryItems(String deliveryPersonId, String orderId);

    void updateDeliveryCount(String deliveryPersonId);

    void removeReturnItemFromDeliveryPerson(String deliveryPersonId, String orderId);

    void removeExchangeItemFromDeliveryPerson(String deliveryPersonId, String orderId);

    String deleteDeliveryMan(String id);

    DeliveryPersonResponse getDeliveryPersonByOrderId(String orderId);

    void updateDeliveryCountAfterOrderCancellation(String deliveryPersonId);

    DeliveryPerson save(DeliveryPerson deliveryPerson);

    long totalCount();

    DeliveryPerson getDeliveryPeronData();

    public DeliveryPerson updateDelivery(DeliveryPerson deliveryPerson);
}
