package com.ECommerceApp.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {

    private int status;             // HTTP status code
    private String error;           // Short error type (e.g., Bad Request)
    private String message;         // Detailed message
    private String path;            // Endpoint path (optional)
    private LocalDateTime timestamp;

}
