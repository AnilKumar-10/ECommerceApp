package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.NotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface NotificationLogRepository extends MongoRepository<NotificationLog,String> {

    List<NotificationLog> findByUserId(String userId);

    List<NotificationLog> findByTypeIgnoreCase(String type);

    long countByTypeIgnoreCase(String type);

    void deleteByUserId(String userId);

    @Query(value = "{ 'userId': ?0 }", sort = "{ '_id': -1 }")
    List<NotificationLog> findTopNByUserIdOrderByIdDesc(String userId, int limit);

}
