package com.ECommerceApp.ServiceInterface.User;

import com.ECommerceApp.DTO.Delivery.DeliveryItems;
import com.ECommerceApp.DTO.ReturnAndExchange.ExchangeDeliveryItems;
import com.ECommerceApp.DTO.ReturnAndExchange.ProductExchangeInfo;
import com.ECommerceApp.DTO.ReturnAndExchange.ProductReturnDetails;
import com.ECommerceApp.DTO.ReturnAndExchange.RefundAndReturnResponse;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Delivery.ShippingDetails;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Model.Product.StockLog;
import com.ECommerceApp.Model.RefundAndExchange.NotificationLog;

public interface IEmailService {

    void sendOrderConfirmationEmail(String toEmail, String userName, Order order, ShippingDetails shippingDetails);

    void sendOrderDeliveredEmail(String toEmail, String userName, Order order);

    void sendReturnRequestedEmail(String toEmail, RefundAndReturnResponse dto);

    void sendReturnCompletedEmail(String toEmail, String userName, Order order);

    void sendRefundRejectedEmail(String toEmail, String userName, String orderId);

    void sendOrderCancellationEmail(Order order, String userName, String toEmail);

    void sendOrderAssignedToDeliveryPerson(String toEmail, DeliveryItems deliveryItem, String deliverPersonName, String deliveryPersonId);

    void sendOrderCancellationToDelivery(String deliveryEmail, DeliveryItems deliveryItem, String deliveryPersonId);

    void sendLowStockAlertToSeller(String sellerEmail, StockLog stockLog);

    void sendReturnProductNotificationMail(String toEmail, DeliveryPerson deliveryPerson, ProductReturnDetails returnDto, String userId);

    void sendOtpEmail(String toEmail, String userName, String otp);

    void sendExchangeConfirmationEmail(String toEmail, ProductExchangeInfo exchangeInfo, String upiId);

    void sendExchangeAssignmentMailToDeliveryPerson(String toEmail, DeliveryPerson deliveryPerson, ExchangeDeliveryItems item);

    void saveLogDetails(NotificationLog notificationLog);
}
