package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.InitiatePaymentDto;
import com.ECommerceApp.DTO.PaymentDto;
import com.ECommerceApp.Exceptions.PaymentAmountMissMatchException;
import com.ECommerceApp.Exceptions.PaymentNotFoundException;
import com.ECommerceApp.Model.Order;
import com.ECommerceApp.Model.Payment;
import com.ECommerceApp.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    // this logs the user initiation of payment(online), that may or may not be success. in case any failure occurs this stores that also
    public Payment initiatePayment(InitiatePaymentDto initiatePaymentDto) {
        System.out.println("inside the initiate payment : "+initiatePaymentDto);
        Payment payment = new Payment();
        Order order = orderService.getOrder(initiatePaymentDto.getOrderId());
        if(order.getFinalAmount() != initiatePaymentDto.getAmount()){
            throw new PaymentAmountMissMatchException("Amount to be paid is not matched");
        }
        long nextId = sequenceGeneratorService.getNextSequence("paymentId");
        payment.setId(String.valueOf(nextId)); // If id is String
        payment.setOrderId(initiatePaymentDto.getOrderId());
        payment.setUserId(initiatePaymentDto.getUserId());
        payment.setAmountPaid(initiatePaymentDto.getAmount());
        payment.setPaymentMethod(initiatePaymentDto.getMethod());
        payment.setStatus("PENDING");// because we dont know whether the payment will be done or not
        payment.setTransactionTime(new Date());
        return paymentRepository.save(payment);
    }

    // 2. Update payment on success
    public Payment confirmUPIPayment(PaymentDto paymentDto) {
        Payment payment = paymentRepository.findById(paymentDto.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        payment.setTransactionId(paymentDto.getTransactionId());
        payment.setStatus("SUCCESS");
        payment.setTransactionTime(new Date());
        paymentRepository.save(payment);

        // Update order as well
        orderService.markOrderAsPaid(payment.getOrderId(), payment.getId());
        return payment; // after this the flow goes to the shipping details.
    }

    // 3. Update payment on failure
    public Payment failPayment(PaymentDto paymentDto) {
        Payment payment = paymentRepository.findById(paymentDto.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        payment.setStatus("FAILED");
        payment.setTransactionTime(new Date());
        paymentRepository.save(payment);
        // Mark order as failed
        orderService.markOrderAsPaymentFailed(payment.getOrderId());
        return payment;
    }

    public Payment confirmCODPayment(PaymentDto paymentDto) { // for COD payment
        Payment payment = paymentRepository.findById(paymentDto.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        payment.setTransactionId(paymentDto.getTransactionId());
        payment.setStatus("SUCCESS");
        payment.setTransactionTime(new Date());
        return paymentRepository.save(payment); // after this the flow goes to the shipping details.
    }


    public Payment getPaymentById(String paymentId){
        return paymentRepository.findById(paymentId).orElseThrow(()-> new PaymentNotFoundException("Payment not found"));
    }

    public List<Payment> getAllFailedPayments() {
        return paymentRepository.findAllFailedPayements();
    }
}
