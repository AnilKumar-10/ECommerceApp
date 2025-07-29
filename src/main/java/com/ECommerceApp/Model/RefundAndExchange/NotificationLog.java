package com.ECommerceApp.Model.RefundAndExchange;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class NotificationLog {
    @Id
    private String id;
    private String userId;
    private String message;
    private NotificationType type;
    private Date createdAt;

    public enum NotificationType {
        ORDER,
        ALERT,
        DELIVERY,
        REFUND,
        EXCHANGE,
        CANCEL
    }
}

