package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.Delivery.DeliveryItems;
import com.ECommerceApp.DTO.Delivery.DeliveryPersonResponse;
import com.ECommerceApp.Model.Delivery.DeliveryHistory;
import com.ECommerceApp.Repository.DeliveryHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
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
        log.info("saving the delivery history after delivery is success");
        deliveryHistoryRepository.save(deliveryHistory);

    }


    public List<DeliveryHistory> getDeliveryHistoryByDeliveryPersonId(String deliveryPersonId){
        return deliveryHistoryRepository.findByDeliveryPersonId(deliveryPersonId);
    }
}

