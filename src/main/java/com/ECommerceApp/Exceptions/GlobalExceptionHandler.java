package com.ECommerceApp.Exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

// You may import your exception classes from your package here
// import com.yourpackage.exceptions.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ExceptionResponse> buildResponse(Exception ex, HttpStatus status, String path) {
        ExceptionResponse response = new ExceptionResponse(status.value(), status.getReasonPhrase(), ex.getMessage(), path, LocalDateTime.now());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler({
            AddressNotFoundException.class,
            CategoryNotFoundException.class,
            CouponNotFoundException.class,
            DeliveryNotFoundException.class,
            InvoiceNotFoundException.class,
            OrderCancellationExpiredException.class,
            OrderNotFoundException.class,
            PaymentNotFoundException.class,
            ProductNotFoundException.class,
            RefundNotFoundException.class,
            ReviewNotFountException.class,
            RootCategoryNotFoundException.class,
            ShippingDetailsNotFoundException.class,
            ShippingNotFoundException.class,
            TaxRuleNotFoundException.class,
            UnknowUserReviewException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<ExceptionResponse> handleNotFoundExceptions(Exception ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    @ExceptionHandler({
            InValidCouponException.class,
            ProductOutOfStockException.class
    })
    public ResponseEntity<ExceptionResponse> handleBadRequestExceptions(Exception ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    // Generic fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }
}

