package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.RaiseRefundRequestDto;
import com.ECommerceApp.DTO.RefundAndReturnResponseDTO;
import com.ECommerceApp.Exceptions.RefundNotFoundException;
import com.ECommerceApp.Model.*;
import com.ECommerceApp.Repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.net.Authenticator;
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
                refundAmount += item.getPrice()* item.getQuantity();

            }
        }
        refundAmount = totalAmount-refundAmount;
        System.out.println("payment: "+order.getPaymentId());
        Refund refund = new Refund();
        refund.setRefundId(String.valueOf(sequenceGeneratorService.getNextSequence("refundId")));
        refund.setUserId(order.getBuyerId());
        refund.setOrderId(refundRequestDto.getOrderId());
        refund.setPaymentId(order.getPaymentId());
        refund.setReason(refundRequestDto.getReason());
        System.out.println("reason in refund class: "+refundRequestDto.getReason());
        refund.setRefundAmount(refundAmount);
        refund.setStatus("PENDING");
        refund.setRequestedAt(new Date());
        System.out.println("inside the refund service class");
        ShippingDetails shippingDetails = returnService.updateShippingStatusForRefundAndReturn(order.getId()); // this will update the status in shipping details
        returnService.updateOrderItemsForReturn(orderItems,refundRequestDto);
        DeliveryPerson deliveryPerson = returnService.assignReturnProductToDeliveryPerson(shippingDetails,refundRequestDto.getReason()); //  this will asign the delivery person to pick the items
        Refund refund1 = refundRepository.save(refund);
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

        return refundRepository.save(refund);
    }

    //4. Complete the refund after payment reversal (finance system or admin)
    public Refund completeRefund(String refundId) {
        Refund refund = getRefundById(refundId);
        if (!refund.getStatus().equals("APPROVED")) {
            throw new IllegalStateException("Only APPROVED refunds can be completed");
        }
        refund.setStatus("COMPLETED");
        refund.setProcessedAt(new Date());

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
                .and("modificationLogs.newValue").is("delivered"));
        System.out.println("inside the getdeliverydate");
        query.fields().include("modificationLogs.$");

        ShippingDetails result = mongoTemplate.findOne(query, ShippingDetails.class);
        System.out.println("with : "+result);
        if (result != null && result.getModificationLogs() != null && !result.getModificationLogs().isEmpty()) {
            System.out.println("inside if");
            return result.getModificationLogs().get(0).getModifiedAt();
        }
        return null;
    }

}
