package com.ECommerceApp.ServiceInterface.Delivery;

import com.ECommerceApp.Model.Delivery.DeliveryHistory;

import java.util.List;

public interface IDeliveryHistoryService {

    void insertDelivery(String orderId, String deliveryPersonId);

    List<DeliveryHistory> getDeliveryHistoryByDeliveryPersonId(String deliveryPersonId);
}
