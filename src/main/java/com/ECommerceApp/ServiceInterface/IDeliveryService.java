package com.ECommerceApp.ServiceInterface;

import com.ECommerceApp.DTO.Delivery.DeliveryPersonRegistrationRequest;
import com.ECommerceApp.DTO.Delivery.DeliveryPersonRegistrationResponse;
import com.ECommerceApp.DTO.Delivery.DeliveryPersonResponse;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Order.Order;

import java.util.List;

public interface IDeliveryService {

    DeliveryPerson assignDeliveryPerson(String deliveryAddress);

    DeliveryPersonRegistrationResponse register(DeliveryPersonRegistrationRequest deliveryPersonRegistrationDto);

    String registerPersons(List<DeliveryPersonRegistrationRequest> deliveryPerson);

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
}
