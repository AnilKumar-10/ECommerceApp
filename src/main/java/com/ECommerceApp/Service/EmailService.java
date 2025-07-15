package com.ECommerceApp.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
}
