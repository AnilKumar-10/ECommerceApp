package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification,String> {
}
