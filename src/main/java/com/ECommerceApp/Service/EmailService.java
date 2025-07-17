package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.RefundAndReturnResponseDTO;
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

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;

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
            System.out.println("The Email is sent successfully.!");

        } catch (MessagingException e) {
            e.printStackTrace(); // replace with logger in production
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
        } catch (Exception e) {
            System.err.println("Failed to send delivery email: " + e.getMessage());
            e.printStackTrace();
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

        } catch (Exception e) {
            e.printStackTrace();
            // Optionally log or throw custom exception
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

            helper.setText(htmlContent, true); // true means it's HTML

            mailSender.send(message);
            System.out.println("mail send to: "+toEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            // Optionally log or rethrow as custom exception
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

            // Process HTML template
            String htmlContent = templateEngine.process("ReturnRejected.html", context);

            // Set email content
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
            System.out.println("Refund rejected email sent to " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Failed to send refund rejected email: " + e.getMessage());
            e.printStackTrace();
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
            helper.setText(htmlContent, true); // true for HTML content

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send order cancellation email to " + toEmail, e);
        }
    }


}
