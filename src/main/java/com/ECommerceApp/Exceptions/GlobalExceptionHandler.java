package com.ECommerceApp.Exceptions;
import com.ECommerceApp.Exceptions.Delivery.DeliveryNotFoundException;
import com.ECommerceApp.Exceptions.Delivery.DeliveryPersonNotFound;
import com.ECommerceApp.Exceptions.Notification.MailSendException;
import com.ECommerceApp.Exceptions.Order.*;
import com.ECommerceApp.Exceptions.Payment.InvoiceNotFoundException;
import com.ECommerceApp.Exceptions.Payment.PaymentNotFoundException;
import com.ECommerceApp.Exceptions.ReturnAndRefund.RefundNotFoundException;
import com.ECommerceApp.Exceptions.Order.TaxRuleNotFoundException;
import com.ECommerceApp.Exceptions.Product.*;
import com.ECommerceApp.Exceptions.User.AddressNotFoundException;
import com.ECommerceApp.Exceptions.User.UnknowUserReviewException;
import com.ECommerceApp.Exceptions.User.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // handles all the invalid fields that are provided at the insertion time.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({
            AddressNotFoundException.class,
            CategoryNotFoundException.class,
            DeliveryPersonNotFound.class,
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
            UserNotFoundException.class,
            ReviewAlreadyExistsException.class
    })
    public ResponseEntity<ExceptionResponse> handleNotFoundExceptions(Exception ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    @ExceptionHandler({
            InValidCouponException.class,
            UnknowUserReviewException.class,
            ProductOutOfStockException.class
    })
    public ResponseEntity<ExceptionResponse> handleBadRequestExceptions(Exception ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, request.getRequestURI());
    }


    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<ExceptionResponse> handleMailSendException(MailSendException ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    private ResponseEntity<ExceptionResponse> buildResponse(Exception ex, HttpStatus status, String path) {
        ExceptionResponse response = new ExceptionResponse(status.value(), status.getReasonPhrase(), ex.getMessage(), path, LocalDateTime.now());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }
}

