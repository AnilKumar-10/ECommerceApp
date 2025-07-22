package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.DeliveryItems;
import com.ECommerceApp.DTO.DeliveryPersonResponse;
import com.ECommerceApp.Model.DeliveryHistory;
import com.ECommerceApp.Repository.DeliveryHistoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryHistoryService {

    @Autowired
    private DeliveryHistoryRepository deliveryHistoryRepository;
    @Autowired
    private DeliveryService deliveryService;

    public void insertDelivery(String orderId, String deliveryPersonId){
        DeliveryPersonResponse deliveryPersonResponseDto = deliveryService.getDeliveryPersonByOrderId(orderId);
        DeliveryItems deliveryItems = deliveryPersonResponseDto.getToDeliveryItems().getFirst();
        DeliveryHistory deliveryHistory = new DeliveryHistory();
        BeanUtils.copyProperties(deliveryItems,deliveryHistory);
        deliveryHistory.setDeliverPersonId(deliveryPersonId);
        deliveryHistory.setName(deliveryPersonResponseDto.getName());
        deliveryHistoryRepository.save(deliveryHistory);

    }


    public List<DeliveryHistory> getDeliveryHistoryByDeliveryPersonId(String deliveryPersonId){
        return deliveryHistoryRepository.findByDeliveryPersonId(deliveryPersonId);
    }
}

