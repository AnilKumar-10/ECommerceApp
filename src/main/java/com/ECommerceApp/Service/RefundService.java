package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.RefundRequestDto;
import com.ECommerceApp.Exceptions.RefundNotFoundException;
import com.ECommerceApp.Model.Order;
import com.ECommerceApp.Model.Refund;
import com.ECommerceApp.Repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    //1. Raising the refund request
    public Refund requestRefund(RefundRequestDto refundRequestDto) {
        Order order = orderService.getOrder(refundRequestDto.getOrderId());
        System.out.println("payment: "+order.getPaymentId());
        Refund refund = new Refund();
        refund.setId(String.valueOf(sequenceGeneratorService.getNextSequence("refundId")));
        refund.setUserId(order.getBuyerId());
        refund.setOrderId(refundRequestDto.getOrderId());
        refund.setPaymentId(order.getPaymentId());
        refund.setReason(refundRequestDto.getReason());
        refund.setRefundAmount(order.getFinalAmount());
        refund.setStatus("PENDING");
        refund.setRequestedAt(new Date());

        return refundRepository.save(refund);
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
    public List<Refund> getRefundsByOrderId(String orderId) {
        return refundRepository.findByOrderId(orderId);
    }

     // 9. Delete refund request (admin or system cleanup)
    public void deleteRefund(String refundId) {
        if (!refundRepository.existsById(refundId)) {
            throw new RefundNotFoundException("Refund not found");
        }
        refundRepository.deleteById(refundId);
    }

}
