package com.ECommerceApp.ServiceInterface;

import com.ECommerceApp.Model.RefundAndExchange.NotificationLog;

import java.util.List;

public interface INotificationLogService {

    void saveNotification(String userId, String message, String type);

    NotificationLog save(NotificationLog log);

    List<NotificationLog> getAllLogs();

    List<NotificationLog> getLogsByUserId(String userId);

    List<NotificationLog> getLogsByType(String type);

    NotificationLog getById(String id);

    void deleteById(String id);

    void deleteByUserId(String userId);

    long countByType(String type);

    List<NotificationLog> getLatestLogsForUser(String userId, int limit);
}
