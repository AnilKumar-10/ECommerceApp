package com.ECommerceApp.Model.User;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class UserActivityLog {
    @Id
    private String id;
    private String userId;
    private String action; // e.g., "LOGIN_SUCCESS", "PASSWORD_CHANGE"
    private String description;
    private Date timestamp;
}
