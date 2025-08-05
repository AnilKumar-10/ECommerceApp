package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Delivery.DeliveryHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DeliveryHistoryRepository extends MongoRepository<DeliveryHistory,String > {

    @Query("{'deliverPersonId':?0}")
    List<DeliveryHistory> findByDeliveryPersonId(String deliveryPersonId);
}
