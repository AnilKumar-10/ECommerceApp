package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.Payment.InitiatePaymentRequest;
import com.ECommerceApp.DTO.Payment.PaymentRequest;
import com.ECommerceApp.Exceptions.Payment.PaymentAmountMissMatchException;
import com.ECommerceApp.Exceptions.Payment.PaymentNotFoundException;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.Repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    // this logs the user initiation of payment(online), that may or may not be success. in case any failure occurs this stores that also
    public Payment initiatePayment(InitiatePaymentRequest initiatePaymentDto) {
        log.info("Initating the online payment for order ");
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
    public Payment confirmUPIPayment(PaymentRequest paymentDto) {
        log.info("making the payment success after initialize");
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
    public Payment failPayment(PaymentRequest paymentDto) {
        log.warn("the online payment is failed");
        Payment payment = paymentRepository.findById(paymentDto.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        payment.setStatus("FAILED");
        payment.setTransactionTime(new Date());
        paymentRepository.save(payment);
        // Mark order as failed
        orderService.markOrderAsPaymentFailed(payment.getOrderId());
        return payment;
    }

    public Payment confirmCODPayment(PaymentRequest paymentDto) { // for COD payment
        log.info("making the COD payment done by the delivery agent");
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


    public Payment savePayment(Payment payment){
        return paymentRepository.save(payment);
    }



    // for exchange payment.
    public Payment initiateExchangePayment(InitiatePaymentRequest initiatePaymentDto) {
        log.info("Initialize the exchange online payment for order: "+initiatePaymentDto.getOrderId());
        System.out.println("inside the initiate payment : "+initiatePaymentDto);
        Payment payment = new Payment();
        Order order = orderService.getOrder(initiatePaymentDto.getOrderId());
        if(order.getExchangeDetails().getExchangeDifferenceAmount()==initiatePaymentDto.getAmount()){
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
        return savePayment(payment);
    }

    public Payment confirmUPIPaymentForExchange(PaymentRequest paymentDto) {
        log.info( " Confirm the exchange COD payment for order: ");
        Payment payment = paymentRepository.findById(paymentDto.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        payment.setTransactionId(paymentDto.getTransactionId());
        payment.setStatus("SUCCESS");
        payment.setTransactionTime(new Date());
        paymentRepository.save(payment);
        //call the payment succes method to assign the delivery
        return payment;
    }


}
