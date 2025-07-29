package com.ECommerceApp.ServiceImplementation;

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
import com.ECommerceApp.Model.Product.StockLogModification;
import com.ECommerceApp.Model.RefundAndExchange.NotificationLog;
import com.ECommerceApp.Model.User.Address;
import com.ECommerceApp.ServiceInterface.IEmailService;
import com.ECommerceApp.ServiceInterface.INotificationLogService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

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
        MimeMessage message = mailSender.createMimeMessage();
        log.info("Sending the order Confirmation mail to :"+toEmail);
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
            System.out.println("Order Confirmation Email is sent successfully  to: "+toEmail);

            NotificationLog log = new NotificationLog();
            log.setUserId(order.getBuyerId());
            log.setMessage("Your Order #" + order.getId() + " is Confirmed");
            log.setType(NotificationLog.NotificationType.ORDER);
            saveLogDetails(log);

        } catch (MessagingException e) {
            throw new MailSendException("Failed to send Order Confirmation Mail.");
        }
    }



    public void sendOrderDeliveredEmail(String toEmail, String userName, Order order) {
        log.info("Sending the order Delivery completed mail to :"+toEmail);
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

            NotificationLog log = new NotificationLog();
            log.setUserId(order.getBuyerId());
            log.setMessage("Order Delivered - #" + order.getId());
            log.setType(NotificationLog.NotificationType.ORDER);
            saveLogDetails(log);
        } catch (Exception e) {
            System.err.println("Failed to send delivery email: " + e.getMessage());
            throw new MailSendException("Failed to send Order Delivered Mail.");
        }
    }


    public void sendReturnRequestedEmail(String toEmail, RefundAndReturnResponse dto) {
        log.info("Sending the order return request mail to :"+toEmail);
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
            System.out.println("Return request mail sent successfully to "+toEmail);
            NotificationLog log = new NotificationLog();
            log.setUserId(dto.getUserId());
            log.setMessage("Your Return Request for Order #" + dto.getOrderId());
            log.setType(NotificationLog.NotificationType.REFUND);
            saveLogDetails(log);

        } catch (Exception e) {
            throw new MailSendException("Failed to send Return Request Mail.");

        }
    }



    public void sendReturnCompletedEmail(String toEmail, String userName, Order order) {
        log.info("Sending the return Completed mail to :"+toEmail);
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
            System.out.println("Return Completed mail send to: "+toEmail);

            NotificationLog log = new NotificationLog();
            log.setUserId(order.getBuyerId());
            log.setMessage("Your Return is Completed – Refund Processing");
            log.setType(NotificationLog.NotificationType.REFUND);
            saveLogDetails(log);

        } catch (MessagingException e) {
            throw new MailSendException("Failed to send Return Completion Mail.");
        }
    }


    public void sendRefundRejectedEmail(String toEmail, String userName, String orderId) {
        log.info("Sending the return rejected mail to :"+toEmail);
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

            NotificationLog log = new NotificationLog();
            log.setUserId(userName);
            log.setMessage("Refund Request Rejected for Order #" + orderId);
            log.setType(NotificationLog.NotificationType.REFUND);
            saveLogDetails(log);


        } catch (MessagingException e) {
            throw  new MailSendException("Failed to send Refund Rejection mail.");
        }
    }


    public void sendOrderCancellationEmail(Order order, String userName, String toEmail) {
        log.info("Sending the order cancelation mail to User :"+toEmail);
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
            System.out.println("Order cancellation Email sent successfully to: "+toEmail);

            NotificationLog log = new NotificationLog();
            log.setUserId(order.getBuyerId());
            log.setMessage("Order Cancellation - " + order.getId());
            log.setType(NotificationLog.NotificationType.CANCEL);
            saveLogDetails(log);

        } catch (MessagingException e) {
            throw new MailSendException("Failed to send order cancellation email to " + toEmail);
        }
    }



    // To notify the deliveryPerson about the new Order.
    public void sendOrderAssignedToDeliveryPerson(String toEmail, DeliveryItems deliveryItem,String deliverPersonName,String deliveryPersonId) {
        log.info("Sending the order assigned mail to delivery agent :"+toEmail);
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
            System.out.println("Order assigned to delivery Email sent successfully to: "+toEmail);

            NotificationLog log = new NotificationLog();
            log.setUserId(deliveryPersonId);
            log.setMessage("New Order Assigned for Delivery"+deliveryItem.getOrderId());
            log.setType(NotificationLog.NotificationType.DELIVERY);
            saveLogDetails(log);

        } catch (Exception e) {
            throw new MailSendException("Failed to send delivery assignment email"+e);
        }
    }

    // to notify the delivery person about the order cancellation.
    public void sendOrderCancellationToDelivery(String deliveryEmail, DeliveryItems deliveryItem,String deliveryPersonId) {
        log.info("Sending the order cancellation mail to delivery agent:"+deliveryEmail);
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
            System.out.println("Delivery cancellation Email sent successfully to: "+deliveryEmail);

            NotificationLog log = new NotificationLog();
            log.setUserId(deliveryPersonId);
            log.setMessage("Order Cancelled – No Delivery Required for Order: " + deliveryItem.getOrderId());
            log.setType(NotificationLog.NotificationType.DELIVERY);
            saveLogDetails(log);

        } catch (MessagingException e) {
            throw new MailSendException("Failed to send order cancellation email to delivery person"+e);
        }
    }



    public void sendLowStockAlertToSeller(String sellerEmail, StockLog stockLog) {
        log.info("Sending the Low Stock alert mail to seller :"+sellerEmail);
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

            NotificationLog log = new NotificationLog();
            log.setUserId(stockLog.getSellerId());
            log.setMessage("Low Stock Alert for Product: "+stockLog.getProductId());
            log.setType(NotificationLog.NotificationType.ALERT);
            saveLogDetails(log);

        } catch (MessagingException e) {
            System.err.println("Failed to send low stock alert: " + e.getMessage());
            throw new RuntimeException("Failed to send low stock email", e);
        }
    }


    public void sendReturnProductNotificationMail(String toEmail, DeliveryPerson deliveryPerson, ProductReturnDetails returnDto, String userId) {
        log.info("Sending the return order to collect mail to delivery agent :"+toEmail);
        try {
            System.out.println("INSIDE MAIL SERIVE: "+returnDto);
            Context context = new Context();
            context.setVariable("deliveryPerson", deliveryPerson);
            context.setVariable("returnDto", returnDto);

            String htmlContent = templateEngine.process("ReturnOrderDelivery", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(toEmail);
            helper.setSubject("Return Pickup Assigned - Order " + returnDto.getOrderId());
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

            NotificationLog log = new NotificationLog();
            log.setUserId(userId);
            log.setMessage("Return Pickup Assigned - Order " + returnDto.getOrderId());
            log.setType(NotificationLog.NotificationType.REFUND);
            saveLogDetails(log);


            System.out.println("Return assigned mail to delivery sent successfully: "+toEmail);
        } catch (MessagingException e) {
            throw new MailSendException("Failed to send return pickup notification to delivery person"+e);
        }
    }

    // to send the user otp while resetting the password.
    public void sendOtpEmail(String toEmail, String userName, String otp) {
        log.info("sending the otp mail to user for password reset : "+toEmail);
        try {
            // Prepare the email context
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("otp", otp);

            // Process the Thymeleaf template
            String htmlContent = templateEngine.process("OtpMail.html", context);

            // Build the email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Your OTP to Reset Password");
            helper.setText(htmlContent, true); // true for HTML content

            mailSender.send(mimeMessage);
            log.info("OTP mail sent successfully to " + toEmail);

            NotificationLog log = new NotificationLog();
            log.setUserId(userName);
            log.setMessage("Your OTP to Reset Password");
            log.setType(NotificationLog.NotificationType.ORDER);
            saveLogDetails(log);
        } catch (MessagingException e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
            // Handle/log exception or throw custom exception
        }
    }

    // Sends the exchange information mail to the customer.
    public void sendExchangeConfirmationEmail(String toEmail, ProductExchangeInfo exchangeInfo, String upiId) {
        log.info("Sending the exchange confirmation mail to the customer ");
        try {
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
            context.setVariable("upi",upiId);
            String htmlContent = templateEngine.process("ExchangeRequestConfirmation.html", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Your Exchange Request has been Accepted!");
            helper.setText(htmlContent, true); // true = isHtml

            mailSender.send(mimeMessage);
            System.out.println("Exchange confirmation email sent to " + toEmail);

            NotificationLog log = new NotificationLog();
            log.setUserId(exchangeInfo.getOrderId());
            log.setMessage("Your Exchange Request has been Accepted!"+exchangeInfo.getOrderId());
            log.setType(NotificationLog.NotificationType.EXCHANGE);
            saveLogDetails(log);
        } catch (Exception e) {
            System.err.println("Failed to send exchange email: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void sendExchangeAssignmentMailToDeliveryPerson(String toEmail ,DeliveryPerson deliveryPerson, ExchangeDeliveryItems item) {
        log.info("Sending the exchange order assigned mail to delivery agent");
        try {
            Context context = new Context();
            context.setVariable("deliveryPersonName", deliveryPerson.getName());
            context.setVariable("orderId", item.getOrderId());
            context.setVariable("userName", item.getUserName());
            context.setVariable("productIdToPick", item.getProductIdToPick());
            context.setVariable("productIdToReplace", item.getProductIdToReplace());
            context.setVariable("amount", item.getAmount());
            context.setVariable("paymentMode", item.getPaymentMode());
            context.setVariable("address", item.getAddress());

            String body = templateEngine.process("ExchangeAssignedToDeliver.html", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);  // You can update this method as per your logic
            helper.setSubject("New Exchange Delivery Assignment - Order ID: " + item.getOrderId());
            helper.setText(body, true); // true = HTML

            mailSender.send(message);

            NotificationLog log = new NotificationLog();
            log.setUserId(deliveryPerson.getId());
            log.setMessage("New Exchange Delivery Assignment - Order ID: " + item.getOrderId());
            log.setType(NotificationLog.NotificationType.EXCHANGE);
            saveLogDetails(log);

            System.out.println("Exchange assignment mail sent to: " + deliveryPerson.getName());

        } catch (Exception e) {
            System.err.println("Failed to send exchange delivery mail: " + e.getMessage());
        }
    }

    // storing all the email log details.
    public void saveLogDetails(NotificationLog notificationLog){
        log.info("Saving all the Notification logs into db");
        notificationLogService.saveNotification(notificationLog);
    }
}
