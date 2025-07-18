package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.*;
import com.ECommerceApp.Exceptions.OrderCancellationExpiredException;
import com.ECommerceApp.Exceptions.RefundNotFoundException;
import com.ECommerceApp.Model.*;
import com.ECommerceApp.Repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.*;

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


    //1. Raising the refund request
    public RefundAndReturnResponseDTO requestRefund(RaiseRefundRequestDto refundRequestDto) {
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
        System.out.println("reason in refund class: "+refundRequestDto.getReason());
        double amount = Math.round(refundAmount * 100.0) / 100.0;
        refund.setRefundAmount(amount);
        refund.setStatus("PENDING");
        refund.setRequestedAt(new Date());
        System.out.println("inside the refund service class");
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
        return returnService.getRefundAndReturnRepsonce(deliveryPerson,refund1); // this will return the refund and return details of the product
    }

    //2. Approve the refund request (admin) if the reason is genuine
    public Refund approveRefund(String refundId, String adminId) {
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
        deliveryService.removeReturnItemFromDeliveryPerson(returnUpdate.getDeliveryPersonId(),returnUpdate.getOrderId());
        // this will remove the return product details from the delivery persons to return fields.
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
        Order order = orderService.getOrder(orderId);
        if(order.getOrderStatus().equalsIgnoreCase("SHIPPED")){
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
        deliveryService.removeDeliveredOrderFromToDeliveryItems(shippingService.getShippingByOrderId(orderId).getDeliveryPersonId(),orderId);
        deliveryService.updateDeliveryCountAfterOrderCancellation(shippingService.getShippingByOrderId(orderId).getDeliveryPersonId());
        // sends the mail about the order cancellation to user
        emailService.sendOrderCancellationEmail(order1,"Anil","iamanil3121@gmail.com");
        // sends the mail to the delivery person who the order delivery is assigned about the order cancellation.
        DeliveryPersonResponseDto deliveryPerson =deliveryService.getDeliveryPersonByOrderId(orderId);
        emailService.sendOrderCancellationToDelivery("iamanil3121@gmail.com",
                deliveryPerson.getToDeliveryItems().getFirst(),deliveryPerson.getId());
        return order1;
    }



    public Refund refundOverOrderCancellation(Order order){
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

}
