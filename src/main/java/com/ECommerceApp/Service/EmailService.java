package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.DeliveryItems;
import com.ECommerceApp.DTO.RefundAndReturnResponseDTO;
import com.ECommerceApp.Exceptions.MailSendException;
import com.ECommerceApp.Repository.NotificationLogRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.*;
import com.ECommerceApp.Model.*;

import javax.xml.parsers.SAXParser;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private NotificationLogService notificationLogService;

    public void sendOrderConfirmationEmail(String toEmail, String userName, Order order, ShippingDetails shippingDetails) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Your Order #" + order.getId() + " is Confirmed");

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

            // Shipping info
            context.setVariable("courierName", shippingDetails.getCourierName());
            context.setVariable("trackingId", shippingDetails.getTrackingId());
            context.setVariable("expectedDate", shippingDetails.getExpectedDate());

            // Address
            Address address = shippingDetails.getDeliveryAddress();
            context.setVariable("address", address);

            String htmlContent = templateEngine.process("Order_Confirmed.html", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("The Email is sent successfully  to: "+toEmail);

            saveLogDetails(order.getBuyerId(),"Your Order #" + order.getId() + " is Confirmed" ,"ORDER");

        } catch (MessagingException e) {
            throw new MailSendException("Failed to send Order Confirmation Mail.");
        }
    }



    public void sendOrderDeliveredEmail(String toEmail, String userName, Order order) {
        try {
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

            String body = templateEngine.process("Order_Delivered.html", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(toEmail);
            helper.setSubject("Order Delivered - #" + order.getId());
            helper.setText(body, true);

            mailSender.send(mimeMessage);
            System.out.println("Order delivery email sent to " + toEmail);

            saveLogDetails(order.getBuyerId(),"Order Delivered - #" + order.getId(),"ORDER");

        } catch (Exception e) {
            System.err.println("Failed to send delivery email: " + e.getMessage());
            throw new MailSendException("Failed to send Order Delivered Mail.");
        }
    }


    public void sendReturnRequestedEmail(String toEmail, RefundAndReturnResponseDTO dto) {
        try {
            // Prepare the context for Thymeleaf
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

            // Generate the HTML body from the template
            String htmlContent = templateEngine.process("ReturnRequest.html", context);

            // Create the email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setTo(toEmail);
            helper.setSubject("Your Return Request for Order #" + dto.getOrderId());
            helper.setText(htmlContent, true);

            // Send the mail
            mailSender.send(message);
            System.out.println("mail sent successfully to "+toEmail);

            saveLogDetails(dto.getUserId(),"Your Return Request for Order #" + dto.getOrderId(),"REFUND" );

        } catch (Exception e) {
            throw new MailSendException("Failed to send Return Request Mail.");

        }
    }



    public void sendReturnCompletedEmail(String toEmail, String userName, Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Your Return is Completed – Refund Processing");

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("orderId", order.getId());
            context.setVariable("refundId", order.getRefundId());
            context.setVariable("paymentId", order.getPaymentId());
            context.setVariable("refundAmount", order.getRefundAmount());
            context.setVariable("status", "Successful");

            // Process template
            String htmlContent = templateEngine.process("ReturnCompleted.html", context);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("mail send to: "+toEmail);

            saveLogDetails(order.getBuyerId(), "Your Return is Completed – Refund Processing","REFUND");

        } catch (MessagingException e) {
            throw new MailSendException("Failed to send Return Completion Mail.");
        }
    }


    public void sendRefundRejectedEmail(String toEmail, String userName, String orderId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set subject and recipient
            helper.setTo(toEmail);
            helper.setSubject("Refund Request Rejected for Order #" + orderId);

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("orderId", orderId);
//            context.setVariable("reason", reason);

            String htmlContent = templateEngine.process("ReturnRejected.html", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Refund rejected email sent to " + toEmail);

            saveLogDetails(userName,"Refund Request Rejected for Order #" + orderId,"REFUND");

        } catch (MessagingException e) {
            throw  new MailSendException("Failed to send Refund Rejection mail.");
        }
    }


    public void sendOrderCancellationEmail(Order order, String userName, String toEmail) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("order", order);

            String htmlContent = templateEngine.process("OrderCancellation.html", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Order Cancellation - " + order.getId());
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Email sent successfully to: "+toEmail);

            saveLogDetails(order.getBuyerId(), "Order Cancellation - " + order.getId(),"ORDER" );

        } catch (MessagingException e) {
            throw new MailSendException("Failed to send order cancellation email to " + toEmail);
        }
    }



    // To notify the deliveryPerson about the new Order.
    public void sendOrderAssignedToDeliveryPerson(String toEmail, DeliveryItems deliveryItem,String deliverPersonName,String deliveryPersonId) {
        try {
            Context context = new Context();
            context.setVariable("deliveryItem", deliveryItem);
            context.setVariable("ReceiverName",deliverPersonName);
            String htmlContent = templateEngine.process("OrderAssignedToDelivery.html", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setTo(toEmail);
            helper.setSubject("New Order Assigned for Delivery");
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            System.out.println("Email sent successfully to: "+toEmail);

            saveLogDetails(deliveryPersonId,"New Order Assigned for Delivery","ORDER");

        } catch (Exception e) {
            throw new MailSendException("Failed to send delivery assignment email"+e);
        }
    }

    // to notify the delivery person about the order cancellation.
    public void sendOrderCancellationToDelivery(String deliveryEmail, DeliveryItems deliveryItem,String deliveryPersonId) {
        try {
            Context context = new Context();
            context.setVariable("deliveryItem", deliveryItem);

            String body = templateEngine.process("OrderToDeliveryIsCancelled.html", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(deliveryEmail);
            helper.setSubject("Order Cancelled – No Delivery Required for Order: " + deliveryItem.getOrderId());
            helper.setText(body, true);

            mailSender.send(message);
            System.out.println("Email sent successfully to: "+deliveryEmail);

            saveLogDetails(deliveryPersonId,"Order Cancelled – No Delivery Required for Order: " + deliveryItem.getOrderId(),"ORDER");

        } catch (MessagingException e) {
            throw new MailSendException("Failed to send order cancellation email to delivery person"+e);
        }
    }



    public void sendLowStockAlertToSeller(String sellerEmail, StockLog stockLog) {
        try {
            // Prepare the Thymeleaf context
            Context context = new Context();
            context.setVariable("stockLog", stockLog);

            // Generate the email content from HTML template
            String htmlContent = templateEngine.process("LowStockModification.html", context);

            // Create the email message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(sellerEmail);
            helper.setSubject("Low Stock Alert for Product: " + stockLog.getProductId());
            helper.setText(htmlContent, true);

            // Send the email
            mailSender.send(message);
            System.out.println("Low stock alert email sent to seller: " + sellerEmail);

            saveLogDetails(stockLog.getSellerId(),"Low Stock Alert for Product: "+stockLog.getProductId(),"ALERT" );

        } catch (MessagingException e) {
            System.err.println("Failed to send low stock alert: " + e.getMessage());
            throw new RuntimeException("Failed to send low stock email", e);
        }
    }

    // storing all the email log details.
    public void saveLogDetails(String userId,String subject,String type){
        notificationLogService.saveNotification(userId, subject, type);
    }

}
