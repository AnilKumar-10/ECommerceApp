package com.ECommerceApp.ServiceImplementation.Payment;

import com.ECommerceApp.DTO.Payment.InitiatePaymentRequest;
import com.ECommerceApp.DTO.Payment.PaymentRequest;
import com.ECommerceApp.Exceptions.Payment.PaymentAmountMissMatchException;
import com.ECommerceApp.Exceptions.Payment.PaymentNotFoundException;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.Repository.PaymentRepository;
import com.ECommerceApp.ServiceImplementation.Order.SequenceGeneratorService;
import com.ECommerceApp.ServiceInterface.Payment.IPaymentService;
import com.ECommerceApp.ServiceInterface.Order.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class PaymentService implements IPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;


    public Payment initiatePayment(InitiatePaymentRequest request) {
        log.info("Initiating the online payment for order: {}", request.getOrderId());
        Order order = orderService.getOrder(request.getOrderId());
        double expectedAmount = order.getFinalAmount();
        return initiatePaymentInternal(request, expectedAmount);
    }

    public Payment initiateExchangePayment(InitiatePaymentRequest request) {
        log.info("Initializing the exchange online payment for order: {}", request.getOrderId());
        Order order = orderService.getOrder(request.getOrderId());
        double expectedAmount = order.getExchangeDetails().getExchangeDifferenceAmount();
        return initiatePaymentInternal(request, expectedAmount);
    }


    // this logs the user initiation of payment(online), that may or may not be success. in case any failure occurs this stores that also
//    public Payment initiatePayment(InitiatePaymentRequest initiatePaymentDto) {
//        log.info("Initiating the online payment for order ");
//        Order order = orderService.getOrder(initiatePaymentDto.getOrderId());
//        if(order.getFinalAmount() != initiatePaymentDto.getAmount()){
//            throw new PaymentAmountMissMatchException("Amount to be paid is not matched");
//        }
//        Payment payment = new Payment();
//        long nextId = sequenceGeneratorService.getNextSequence("paymentId");
//        payment.setId(String.valueOf(nextId)); // If id is String
//        payment.setOrderId(initiatePaymentDto.getOrderId());
//        payment.setUserId(initiatePaymentDto.getUserId());
//        payment.setAmountPaid(initiatePaymentDto.getAmount());
//        payment.setPaymentMethod(initiatePaymentDto.getMethod());
//        payment.setStatus(Payment.PaymentStatus.PENDING);// because we don't know whether the payment will be done or not
//        payment.setTransactionTime(new Date());
//        return savePayment(payment);
//    }

    // for exchange payment.
//    public Payment initiateExchangePayment(InitiatePaymentRequest initiatePaymentDto) {
//        log.info("Initialize the exchange online payment for order: "+initiatePaymentDto.getOrderId());
//        Payment payment = new Payment();
//        Order order = orderService.getOrder(initiatePaymentDto.getOrderId());
//        if(order.getExchangeDetails().getExchangeDifferenceAmount()!=initiatePaymentDto.getAmount()){
//            throw new PaymentAmountMissMatchException("Amount to be paid is not matched");
//        }
//        long nextId = sequenceGeneratorService.getNextSequence("paymentId");
//        payment.setId(String.valueOf(nextId));
//        payment.setOrderId(initiatePaymentDto.getOrderId());
//        payment.setUserId(initiatePaymentDto.getUserId());
//        payment.setAmountPaid(initiatePaymentDto.getAmount());
//        payment.setPaymentMethod(initiatePaymentDto.getMethod());
//        payment.setStatus(Payment.PaymentStatus.PENDING);// because we don't know whether the payment will be done or not
//        payment.setTransactionTime(new Date());
//        return savePayment(payment);
//    }


    // Update payment on success
    public Payment confirmUPIPayment(PaymentRequest paymentDto) {
        log.info("making the payment success after initialize");
        Payment payment = getPaymentById(paymentDto.getPaymentId());
        payment.setTransactionId(paymentDto.getTransactionId());
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setTransactionTime(new Date());
        savePayment(payment);

        // Update order as well
        orderService.markOrderAsPaid(payment.getOrderId(), payment.getId());
        return payment; // after this the flow goes to the shipping details.
    }

    //  Update payment on failure
    public Payment failPayment(PaymentRequest paymentDto) {
        log.warn("the online payment is failed");
        Payment payment = getPaymentById(paymentDto.getPaymentId());

        payment.setStatus(Payment.PaymentStatus.FAILED);
        payment.setTransactionTime(new Date());
        savePayment(payment);
        orderService.markOrderAsPaymentFailed(payment.getOrderId());
        return payment;
    }

    // conforming the cod payment success.
    public void confirmCODPayment(PaymentRequest paymentDto) { // for COD payment
        log.info("making the COD payment done by the delivery agent");
        Payment payment = getPaymentById(paymentDto.getPaymentId());
        payment.setTransactionId(paymentDto.getTransactionId());
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setTransactionTime(new Date());
        savePayment(payment);
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



    public Payment confirmUPIPaymentForExchange(PaymentRequest paymentDto) {
        log.info( " Confirm the exchange COD payment for order: ");
        Payment payment = paymentRepository.findById(paymentDto.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        payment.setTransactionId(paymentDto.getTransactionId());
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setTransactionTime(new Date());
        savePayment(payment);
        return payment;
    }


    private Payment initiatePaymentInternal(InitiatePaymentRequest request, double expectedAmount) {
        if (expectedAmount != request.getAmount()) {
            throw new PaymentAmountMissMatchException("Amount to be paid is not matched for payment");
        }
        long nextId = sequenceGeneratorService.getNextSequence("paymentId");
        Payment payment = new Payment();
        payment.setId(String.valueOf(nextId));
        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmountPaid(request.getAmount());
        payment.setPaymentMethod(request.getMethod());
        payment.setStatus(Payment.PaymentStatus.PENDING); // initially pending
        payment.setTransactionTime(new Date());

        return savePayment(payment);
    }

}
