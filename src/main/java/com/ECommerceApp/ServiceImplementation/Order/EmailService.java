package com.ECommerceApp.ServiceImplementation.Order;

import com.ECommerceApp.DTO.Delivery.DeliveryItems;
import com.ECommerceApp.DTO.ReturnAndExchange.ExchangeDeliveryItems;
import com.ECommerceApp.DTO.ReturnAndExchange.ProductExchangeInfo;
import com.ECommerceApp.DTO.ReturnAndExchange.ProductReturnDetails;
import com.ECommerceApp.DTO.ReturnAndExchange.RefundAndReturnResponse;
import com.ECommerceApp.Exceptions.Notification.MailSendException;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Delivery.ShippingDetails;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Model.Product.StockLog;
import com.ECommerceApp.Model.RefundAndExchange.NotificationLog;
import com.ECommerceApp.ServiceInterface.User.IEmailService;
import com.ECommerceApp.ServiceInterface.User.INotificationLogService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Slf4j
@Service
public class EmailService implements IEmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private INotificationLogService notificationLogService;

    public void sendOrderConfirmationEmail(String toEmail, String userName, Order order, ShippingDetails shippingDetails) {
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("orderId", order.getId());
        context.setVariable("items", order.getOrderItems());
        context.setVariable("totalAmount", order.getTotalAmount());
        context.setVariable("discount", order.getDiscount());
        context.setVariable("couponId", order.getCouponId());
        context.setVariable("tax", order.getTax());
        context.setVariable("finalAmount", order.getFinalAmount());
        context.setVariable("paymentMethod", order.getPaymentMethod());
        context.setVariable("paymentStatus", order.getPaymentStatus());
        context.setVariable("paymentId", order.getPaymentId());
        context.setVariable("courierName", shippingDetails.getCourierName());
        context.setVariable("trackingId", shippingDetails.getTrackingId());
        context.setVariable("expectedDate", shippingDetails.getExpectedDate());
        context.setVariable("address", shippingDetails.getDeliveryAddress());

        sendEmail(
                toEmail,
                "Your Order #" + order.getId() + " is Confirmed",
                "Order_Confirmed.html",
                context,
                order.getBuyerId(),
                "Your Order #" + order.getId() + " is Confirmed",
                NotificationLog.NotificationType.ORDER
        );
    }




    public void sendOrderDeliveredEmail(String toEmail, String userName, Order order) {
        log.info("Sending the order Delivery completed mail to :{}", toEmail);

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("orderId", order.getId());
        context.setVariable("items", order.getOrderItems());
        context.setVariable("totalAmount", order.getTotalAmount());
        context.setVariable("discount", order.getDiscount());
        context.setVariable("couponId", order.getCouponId());
        context.setVariable("tax", order.getTax());
        context.setVariable("finalAmount", order.getFinalAmount());
        context.setVariable("paymentMethod", order.getPaymentMethod());

        sendEmail(
                toEmail,
                "Order Delivered - #" + order.getId(),
                "Order_Delivered.html",
                context,
                order.getBuyerId(),
                "Order Delivered - #" + order.getId(),
                NotificationLog.NotificationType.ORDER
        );
    }



    public void sendReturnRequestedEmail(String toEmail, RefundAndReturnResponse dto) {
        log.info("Sending the order return request mail to :{}", toEmail);
        Context context = new Context();
        context.setVariable("refundId", dto.getRefundId());
        context.setVariable("userId", dto.getUserId());
        context.setVariable("orderId", dto.getOrderId());
        context.setVariable("paymentId", dto.getPaymentId());
        context.setVariable("refundAmount", dto.getRefundAmount());
        context.setVariable("reason", dto.getReason());
        context.setVariable("status", dto.getStatus());
        context.setVariable("requestedAt", dto.getRequestedAt());
        context.setVariable("processedAt", dto.getProcessedAt());
        context.setVariable("deliveryPersonName", dto.getDeliveryPersonName());
        context.setVariable("expectedPickUpDate", dto.getExpectedPickUpDate());
        context.setVariable("productPicked", dto.isProductPicked());

        sendEmail(
                toEmail,
                "Your Return Request for Order #" + dto.getOrderId(),
                "ReturnRequest.html",
                context,
                dto.getUserId(),
                "Your Return Request for Order #" + dto.getOrderId(),
                NotificationLog.NotificationType.REFUND
        );
    }



    public void sendReturnCompletedEmail(String toEmail, String userName, Order order) {
        log.info("Sending the return Completed mail to :{}", toEmail);
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("orderId", order.getId());
        context.setVariable("refundId", order.getRefundId());
        context.setVariable("paymentId", order.getPaymentId());
        context.setVariable("refundAmount", order.getRefundAmount());
        context.setVariable("status", "Successful");
        sendEmail(
                toEmail,
                "Your Return is Completed – Refund Processing",
                "ReturnCompleted.html",
                context,
                order.getBuyerId(),
                "Your Return is Completed – Refund Processing",
                NotificationLog.NotificationType.REFUND
        );
    }



    public void sendRefundRejectedEmail(String toEmail, String userName, String orderId) {
        log.info("Sending the refund rejected mail to :{}", toEmail);

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("orderId", orderId);

        sendEmail(
                toEmail,
                "Refund Request Rejected for Order #" + orderId,
                "ReturnRejected.html",
                context,
                userName, // careful: here you passed userName as userId before
                "Refund Request Rejected for Order #" + orderId,
                NotificationLog.NotificationType.REFUND
        );
    }



    public void sendOrderCancellationEmail(Order order, String userName, String toEmail) {
        log.info("Sending the order cancellation mail to User :{}", toEmail);

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("order", order);

        sendEmail(
                toEmail,
                "Order Cancellation - " + order.getId(),
                "OrderCancellation.html",
                context,
                order.getBuyerId(),
                "Order Cancellation - " + order.getId(),
                NotificationLog.NotificationType.CANCEL
        );
    }



    // To notify the deliveryPerson about the new Order.
    public void sendOrderAssignedToDeliveryPerson(String toEmail, DeliveryItems deliveryItem, String deliverPersonName, String deliveryPersonId) {
        log.info("Sending the order assigned mail to delivery agent :{}", toEmail);

        Context context = new Context();
        context.setVariable("deliveryItem", deliveryItem);
        context.setVariable("ReceiverName", deliverPersonName);

        sendEmail(
                toEmail,
                "New Order Assigned for Delivery",
                "OrderAssignedToDelivery.html",
                context,
                deliveryPersonId,
                "New Order Assigned for Delivery " + deliveryItem.getOrderId(),
                NotificationLog.NotificationType.DELIVERY
        );
    }


    // to notify the delivery person about the order cancellation.
    public void sendOrderCancellationToDelivery(String deliveryEmail, DeliveryItems deliveryItem, String deliveryPersonId) {
        log.info("Sending the order cancellation mail to delivery agent:{}", deliveryEmail);

        Context context = new Context();
        context.setVariable("deliveryItem", deliveryItem);

        sendEmail(
                deliveryEmail,
                "Order Cancelled – No Delivery Required for Order: " + deliveryItem.getOrderId(),
                "OrderToDeliveryIsCancelled.html",
                context,
                deliveryPersonId,
                "Order Cancelled – No Delivery Required for Order: " + deliveryItem.getOrderId(),
                NotificationLog.NotificationType.DELIVERY
        );
    }




    public void sendLowStockAlertToSeller(String sellerEmail, StockLog stockLog) {
        log.info("Sending the Low Stock alert mail to seller :{}", sellerEmail);

        Context context = new Context();
        context.setVariable("stockLog", stockLog);

        sendEmail(
                sellerEmail,
                "Low Stock Alert for Product: " + stockLog.getProductId(),
                "LowStockNotification.html",
                context,
                stockLog.getSellerId(),
                "Low Stock Alert for Product: " + stockLog.getProductId(),
                NotificationLog.NotificationType.ALERT
        );
    }



    public void sendReturnProductNotificationMail(String toEmail, DeliveryPerson deliveryPerson,ProductReturnDetails returnDto, String userId) {
        log.info("Sending the return order to collect mail to delivery agent : {}", toEmail);

        Context context = new Context();
        context.setVariable("deliveryPerson", deliveryPerson);
        context.setVariable("returnDto", returnDto);

        sendEmail(
                toEmail,
                "Return Pickup Assigned - Order " + returnDto.getOrderId(),
                "ReturnOrderDelivery",   // Thymeleaf template
                context,
                userId,
                "Return Pickup Assigned - Order " + returnDto.getOrderId(),
                NotificationLog.NotificationType.REFUND
        );
    }


    // to send the user otp while resetting the password.
    public void sendOtpEmail(String toEmail, String userName, String otp) {
        log.info("Sending the OTP mail to user for password reset: {}", toEmail);

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("otp", otp);

        sendEmail(
                toEmail,
                "Your OTP to Reset Password",
                "OtpMail.html",
                context,
                userName,  // Using userName as userId in log
                "Your OTP to Reset Password",
                NotificationLog.NotificationType.ORDER
        );
    }


    // Sends the exchange information mail to the customer.
    public void sendExchangeConfirmationEmail(String toEmail, ProductExchangeInfo exchangeInfo, String upiId) {
        log.info("Sending the exchange confirmation mail to the customer: {}", toEmail);

        Context context = new Context();
        context.setVariable("orderId", exchangeInfo.getOrderId());
        context.setVariable("productIdToPick", exchangeInfo.getProductIdToPick());
        context.setVariable("productIdToReplace", exchangeInfo.getProductIdToReplace());
        context.setVariable("amount", exchangeInfo.getAmount());
        context.setVariable("amountPayType", exchangeInfo.getAmountPayType());
        context.setVariable("paymentStatus", exchangeInfo.getPaymentStatus());
        context.setVariable("deliveryPersonName", exchangeInfo.getDeliveryPersonName());
        context.setVariable("expectedReturnDate", exchangeInfo.getExpectedReturnDate());
        context.setVariable("orderPaymentType", exchangeInfo.getOrderPaymentType());
        context.setVariable("upi", upiId);

        sendEmail(
                toEmail,
                "Your Exchange Request has been Accepted!",
                "ExchangeRequestConfirmation.html",
                context,
                exchangeInfo.getOrderId(),
                "Your Exchange Request has been Accepted! " + exchangeInfo.getOrderId(),
                NotificationLog.NotificationType.EXCHANGE
        );
    }



    public void sendExchangeAssignmentMailToDeliveryPerson(String toEmail, DeliveryPerson deliveryPerson, ExchangeDeliveryItems item) {
        log.info("Sending the exchange order assigned mail to delivery agent: {}", deliveryPerson.getName());

        Context context = new Context();
        context.setVariable("deliveryPersonName", deliveryPerson.getName());
        context.setVariable("orderId", item.getOrderId());
        context.setVariable("userName", item.getUserName());
        context.setVariable("productIdToPick", item.getProductIdToPick());
        context.setVariable("productIdToReplace", item.getProductIdToReplace());
        context.setVariable("amount", item.getAmount());
        context.setVariable("paymentMode", item.getPaymentMode());
        context.setVariable("address", item.getAddress());

        sendEmail(
                toEmail,
                "New Exchange Delivery Assignment - Order ID: " + item.getOrderId(),
                "ExchangeAssignedToDeliver.html",
                context,
                deliveryPerson.getId(),
                "New Exchange Delivery Assignment - Order ID: " + item.getOrderId(),
                NotificationLog.NotificationType.EXCHANGE
        );
    }

    // storing all the email log details.
    public void saveLogDetails(NotificationLog notificationLog){
        log.info("Saving all the Notification logs into db");
        notificationLogService.saveNotification(notificationLog);
    }

    private void sendEmail(
            String toEmail,
            String subject,
            String templateName,
            Context context,
            String userId,
            String notificationMessage,
            NotificationLog.NotificationType type) {

        try {
            // Generate HTML body from template
            String htmlContent = templateEngine.process(templateName, context);

            // Create email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Send email
            mailSender.send(message);
            log.info("{} email sent to {}", type, toEmail);

            // Save log
            NotificationLog logEntity = new NotificationLog();
            logEntity.setUserId(userId);
            logEntity.setMessage(notificationMessage);
            logEntity.setType(type);
            saveLogDetails(logEntity);

        } catch (Exception e) {
            log.error("Failed to send {} email to {}: {}", type, toEmail, e.getMessage());
            throw new MailSendException("Failed to send " + type + " email.");
        }
    }

}
