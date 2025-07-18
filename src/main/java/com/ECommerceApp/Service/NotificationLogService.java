package com.ECommerceApp.Service;

import com.ECommerceApp.Exceptions.NotificationNotFoundException;
import com.ECommerceApp.Model.NotificationLog;
import com.ECommerceApp.Repository.NotificationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service

public class NotificationLogService {

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    // Save a new notification
    public NotificationLog saveNotification(String userId, String message, String type) {
        NotificationLog log = new NotificationLog();
        log.setUserId(userId);
        log.setMessage(message);
        log.setType(type);
        log.setCreatedAt(new Date());
        return notificationLogRepository.save(log);
    }

    // Save an existing object
    public NotificationLog save(NotificationLog log) {
        return notificationLogRepository.save(log);
    }

    // Get all notifications
    public List<NotificationLog> getAllLogs() {
        return notificationLogRepository.findAll();
    }

    // Get notifications by userId
    public List<NotificationLog> getLogsByUserId(String userId) {
        return notificationLogRepository.findByUserId(userId);
    }

    // Get notifications by type
    public List<NotificationLog> getLogsByType(String type) {
        return notificationLogRepository.findByTypeIgnoreCase(type);
    }

    // Get by ID
    public NotificationLog getById(String id) {
        return notificationLogRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));
    }

    // Delete by ID
    public void deleteById(String id) {
        if (!notificationLogRepository.existsById(id)) {
            throw new NotificationNotFoundException("Notification not found with id: " + id);
        }
        notificationLogRepository.deleteById(id);
    }

    // Delete by userId
    public void deleteByUserId(String userId) {
        notificationLogRepository.deleteByUserId(userId);
    }

    // Count by type
    public long countByType(String type) {
        return notificationLogRepository.countByTypeIgnoreCase(type);
    }

    // Get latest N notifications for a user
    public List<NotificationLog> getLatestLogsForUser(String userId, int limit) {
        return notificationLogRepository.findTopNByUserIdOrderByIdDesc(userId, limit);
    }
}

