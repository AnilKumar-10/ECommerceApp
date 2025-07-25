package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.Delivery.DeliveryPersonResponse;
import com.ECommerceApp.DTO.ReturnAndExchange.RaiseRefundRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.RefundAndReturnResponse;
import com.ECommerceApp.DTO.ReturnAndExchange.ReturnUpdateRequest;
import com.ECommerceApp.Exceptions.Order.OrderCancellationExpiredException;
import com.ECommerceApp.Exceptions.ReturnAndRefund.RefundNotFoundException;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Delivery.ShippingDetails;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Model.Order.OrderItem;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.Model.RefundAndExchange.Refund;
import com.ECommerceApp.Repository.RefundRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.*;
@Slf4j
@Service
public class RefundService {

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    private RefundRepository refundRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ReturnService returnService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ShippingService shippingService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private EmailService emailService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private StockLogService stockLogService;

    //1. Raising the refund request
    public RefundAndReturnResponse requestRefund(RaiseRefundRequest refundRequestDto) {
        log.info("Requesting for the refund on returning the products: "+refundRequestDto.getProductIds());
        Order order = orderService.getOrder(refundRequestDto.getOrderId());
        if(!order.getOrderStatus().equals("DELIVERED")){
            throw new RuntimeException("The order must be delivered before the refund..");
        }
        List<OrderItem> orderItems = order.getOrderItems();
        double totalAmount = order.getFinalAmount();
        double refundAmount = 0.0;
        for(OrderItem item : orderItems){
            if(refundRequestDto.getProductIds().contains(item.getProductId())){
                checkProductReturnable(item.getProductId(),order.getShippingId());
                refundAmount += item.getPrice()* item.getQuantity() + item.getTax();

            }
        }
        totalAmount = totalAmount-refundAmount;
        System.out.println("payment: "+order.getPaymentId());
        Refund refund = new Refund();
        refund.setRefundId(String.valueOf(sequenceGeneratorService.getNextSequence("refundId")));
        refund.setUserId(order.getBuyerId());
        refund.setOrderId(refundRequestDto.getOrderId());
        refund.setPaymentId(order.getPaymentId());
        refund.setReason(refundRequestDto.getReason());
        double amount = Math.round(refundAmount * 100.0) / 100.0;
        refund.setRefundAmount(amount);
        refund.setStatus("PENDING");
        refund.setRequestedAt(new Date());
        ShippingDetails shippingDetails = returnService.updateShippingStatusForRefundAndReturn(order.getId());
        // this will update the status in shipping details
        returnService.updateOrderItemsForReturn(orderItems,refundRequestDto); //update the status of product to request to return
        DeliveryPerson deliveryPerson = returnService.assignReturnProductToDeliveryPerson(shippingDetails,refundRequestDto.getReason());
        // this will asign the delivery person to pick the items
        Refund refund1 = refundRepository.save(refund);
//        order.setFinalAmount(Math.round(totalAmount * 100.0) / 100.0);
        order.setRefundId(refund1.getRefundId());
        order.setReturned(true);
        orderService.saveOrder(order); // updates the total amount and the return id
        approveRefund(refund1.getRefundId(),"admin");
        return returnService. getRefundAndReturnResponce(deliveryPerson,refund1); // this will return the refund and return details of the product
    }

    //2. Approve the refund request (admin) if the reason is genuine
    public Refund approveRefund(String refundId, String adminId) {
        log.info("Approve the refund for: "+refundId);
        Refund refund = getRefundById(refundId);
        if (!refund.getStatus().equals("PENDING")) {
            throw new IllegalStateException("Only PENDING refunds can be approved");
        }
        refund.setStatus("APPROVED");
        refund.setProcessedAt(new Date());

        return refundRepository.save(refund);
    }

//  3. Reject refund request (admin)
    public Refund rejectRefund(String refundId, String reason) {
        log.info("Rejecting the return and refund");
        Refund refund = getRefundById(refundId);
        if (!refund.getStatus().equals("PENDING")) {
            throw new IllegalStateException("Only PENDING refunds can be rejected");
        }
        refund.setStatus("REJECTED");
        refund.setProcessedAt(new Date());
        refund.setReason(refund.getReason() + " | Rejected: " + reason);
        //  sends the mail after the refund request is rejected.
        emailService.sendRefundRejectedEmail("iamanil3121@gmail.com",refund.getUserId(),refund.getOrderId());
        return refundRepository.save(refund);
    }

    //4. Complete the refund
    public Refund completeRefund(ReturnUpdateRequest returnUpdate) {
        log.info("Making Refund to completed when the product is picked.");
        Refund refund = getRefundsByOrderId(returnUpdate.getOrderId());
        if (!refund.getStatus().equals("APPROVED")) {
            throw new IllegalStateException("Only APPROVED refunds can be completed");
        }
        refund.setStatus("COMPLETED");
        refund.setProcessedAt(new Date());
        Order order = orderService.getOrder(refund.getOrderId());
        double amount = order.getFinalAmount();
        double refundAmount = refund.getRefundAmount();
        amount  = Math.round(amount * 100.0) / 100.0;
        order.setFinalAmount(amount);
        order.setRefundAmount(refundAmount);
        Order order1 = orderService.saveOrder(order); // updating the final amount after the refund is completed.
        emailService.sendReturnCompletedEmail("iamanil3121@gmail.com",order1.getBuyerId(),order1);
        // this will remove the return product details from the delivery persons to return fields.
        deliveryService.removeReturnItemFromDeliveryPerson(returnUpdate.getDeliveryPersonId(),returnUpdate.getOrderId());
//        returnService.updateStockLogAfterOrderCancellation(order1.getId()); // we have to update the stock log  after the order cancellation.
        return refundRepository.save(refund);
    }

     //5. Get refund by ID
    public Refund getRefundById(String refundId) {
        return refundRepository.findById(refundId)
                .orElseThrow(() -> new RefundNotFoundException("Refund not found"));
    }

    //6. Get all refund requests for a user
    public List<Refund> getRefundsByUserId(String userId) {
        return refundRepository.findByUserId(userId);
    }

     //7. Get all refunds by status
    public List<Refund> getRefundsByStatus(String status) {
        return refundRepository.findByStatus(status);
    }


     //8. Get refunds for a specific order
    public Refund getRefundsByOrderId(String orderId) {
        return refundRepository.findByOrderId(orderId);
    }

     // 9. Delete refund request (admin or system cleanup)
    public void deleteRefund(String refundId) {
        if (!refundRepository.existsById(refundId)) {
            throw new RefundNotFoundException("Refund not found");
        }
        refundRepository.deleteById(refundId);
    }

    public void checkProductReturnable(String productId,String shippingId){
        log.info("Checking weather the product is eligible for return or not: "+productId);
        Product product = productService.getProductById(productId);
        int returnBefore = product.getReturnBefore(); // e.g., 7
        Date deliveredDate = getDeliveredTimestamp(shippingId);

        boolean isReturnAllowed = isReturnAvailable(deliveredDate, returnBefore);

        if (isReturnAllowed) {
            System.out.println("Product is eligible for return.");
        } else {
            System.out.println("Return period has expired.");
            throw new RuntimeException(" Return period has expired. ");
        }

    }

    public boolean isReturnAvailable(Date deliveredDate, int returnBeforeDays) {
        log.info("Checking is the return period is expired or not");
        if (deliveredDate == null) {
            throw new IllegalArgumentException("Delivered date is null");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(deliveredDate);
        cal.add(Calendar.DAY_OF_MONTH, returnBeforeDays);
        Date returnExpiryDate = cal.getTime(); // Last returnable date

        Date today = new Date();

        // Check if today is on or before the return expiry date
        return !today.after(returnExpiryDate); // true if return is allowed
    }


    public Date getDeliveredTimestamp(String shippingId) {
        log.info("getting the delivery date of order");
        Query query = new Query(Criteria.where("id").is(shippingId)
                .and("modificationLogs.newValue").is("DELIVERED"));
//        System.out.println("inside the getdeliverydate");
        query.fields().include("modificationLogs.$");

        ShippingDetails result = mongoTemplate.findOne(query, ShippingDetails.class);
//        System.out.println("with : "+result);
        if (result != null && result.getModificationLogs() != null && !result.getModificationLogs().isEmpty()) {
            System.out.println("inside if");
            return result.getModificationLogs().get(0).getModifiedAt();
        }
        return null;
    }



    // ORDER CANCELLATION
    public Order cancelOrder(String orderId,String cancelReason){
        log.warn("Requesting to cancel the order: "+orderId);
        Order order = orderService.getOrder(orderId);
        if(order.getOrderStatus().equalsIgnoreCase("SHIPPED")){
            log.warn("Order cannot be cancelled because the cancellation time is expired.!");
            throw new OrderCancellationExpiredException("Order cannot be cancelled because the cancellation time is expired.!");
        }
        order.setOrderStatus("CANCELLED");
        order.setCancelReason(cancelReason);
        order.setCancelled(true);
        order.setCancellationTime(new Date());
        Order order1 = orderService.saveOrder(order);
        if(order.getPaymentMethod().equalsIgnoreCase("UPI")){
            Refund refund = refundOverOrderCancellation(order1);
        }
        DeliveryPersonResponse deliveryPerson =deliveryService.getDeliveryPersonByOrderId(orderId);
        // sends the mail to the delivery person who the order delivery is assigned about the order cancellation.
        emailService.sendOrderCancellationToDelivery("iamanil3121@gmail.com",
                deliveryPerson.getToDeliveryItems().getFirst(),deliveryPerson.getId());
        deliveryService.removeDeliveredOrderFromToDeliveryItems(shippingService.getShippingByOrderId(orderId).getDeliveryPersonId(),orderId);
        deliveryService.updateDeliveryCountAfterOrderCancellation(shippingService.getShippingByOrderId(orderId).getDeliveryPersonId());
        // sends the mail about the order cancellation to user
        emailService.sendOrderCancellationEmail(order1,"Anil","iamanil3121@gmail.com");
        returnService.updateStockLogAfterOrderCancellation(orderId); // this will update the stock after the order is cancelled.
        return order1;
    }



    public Refund refundOverOrderCancellation(Order order){
        log.info("The payment mode is UPI the order cancellation amount is created back");
        Refund refund = new Refund();
        refund.setOrderId(order.getId());
        refund.setRefundAmount(order.getFinalAmount());
        refund.setStatus("APPROVED");
        refund.setReason(order.getCancelReason());
        refund.setUserId(order.getBuyerId());
        refund.setRequestedAt(order.getCancellationTime());
        refund.setProcessedAt(new Date());
        return refundRepository.save(refund);

    }


    public Refund saveRefund(Refund refund){
        return refundRepository.save(refund);
    }

}
