package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.Order.ShippingUpdateRequest;
import com.ECommerceApp.DTO.Payment.PaymentRequest;
import com.ECommerceApp.DTO.Product.StockLogModificationRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.*;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Order.*;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.Model.RefundAndExchange.Refund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class ExchangeService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private ShippingService shippingService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private StockLogService stockLogService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private TaxRuleService taxRuleService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private PaymentService paymentService;


    public ExchangeResponse exchangeRequest(ProductExchangeRequest productExchangeDto){
        log.info("Request to exchange.");
        Order order = orderService.getOrder(productExchangeDto.getOrderId());
        System.out.println("in exchange method; "+order);
        double oldPrice=0;
        double oldTax=0;
        for(OrderItem orderItem : order.getOrderItems()){
            if(orderItem.getProductId().equalsIgnoreCase(productExchangeDto.getProductIdToReplace())){
                orderItem.setStatus("REQUESTED_TO_RETURN");
                oldPrice = orderItem.getPrice();
                oldTax = orderItem.getTax();
            }
        }

        // Adding the replacement product to the order items
        OrderItem item = createNewOrderItem(order,productExchangeDto);
        double newTax = item.getTax();
        order.setTax(order.getTax() - oldTax + newTax);
        order.getOrderItems().add(item);
        double newPrice = item.getPrice();
        double finalAmount = calculateNewFinalAmount(oldPrice, newPrice, order.getFinalAmount(), order.getTotalAmount(), order.getDiscount());
        order.setTotalAmount(order.getTotalAmount() - oldPrice + newPrice);
        order.setFinalAmount(finalAmount - oldTax + newTax);
        log.info("old price: "+oldPrice+"  , new price; "+newPrice+"  ,new final price: "+finalAmount+ ",  new total price: "+order.getTotalAmount());

        // Updating the exchange details in the order class.
        ExchangeDetails exchangeDetails = updateExchangeDetails(order, productExchangeDto, newPrice, oldPrice);
        System.out.println("exchange details: "+exchangeDetails);
        orderService.saveOrder(order);
        // assign the exchange delivery person.
        if(exchangeDetails.getExchangeType().equalsIgnoreCase("NO_DIFFERENCE") || order.getPaymentMethod().equalsIgnoreCase("COD") || exchangeDetails.getExchangeType().equalsIgnoreCase("REFUNDABLE") ){
            DeliveryPerson deliveryPerson =  assignDeliveryForExchange(order);
            updateNewProductStockToReplace(item); // this will update stock of new product.
            System.out.println("delivery Person after exchange is assigned:"+deliveryPerson);
            ProductExchangeInfo productExchangeInfo = getExchangeInformation(order.getId());
            emailService.sendExchangeConfirmationEmail("iamanil3121@gmail.com",productExchangeInfo,order.getUpiId());
            // here we send the mail about the exchange confirmation because
            // 1. if the order as no difference in prices or exchange amount is to refundable then we have to proceed(No payment required)
            // 2. if the pay type is COD then we have to proceed the exchange process and collect amount after delivery.
        }

        // updating the shipping status.
        ShippingUpdateRequest shippingUpdateDTO = new ShippingUpdateRequest();
        shippingUpdateDTO.setUpdateBy("ADMIN");
        shippingUpdateDTO.setNewValue("REQUEST_TO_EXCHANGE");
        shippingUpdateDTO.setShippingId(order.getShippingId());
        shippingService.updateShippingStatus(shippingUpdateDTO); // this updates the shipping status for exchange.

        return getExchangeInfo(productExchangeDto, exchangeDetails, order);
    }

    // this will get the exchange information
    private ExchangeResponse getExchangeInfo(ProductExchangeRequest productExchangeDto, ExchangeDetails exchangeDetails, Order order) {
        log.info("Getting the exchange response");
        ExchangeResponse exchangeInfo = new ExchangeResponse();
        exchangeInfo.setAmountPayType(exchangeDetails.getExchangeType());
        if(exchangeDetails.getExchangeType().equalsIgnoreCase("NO_DIFFERENCE")){
            exchangeInfo.setAmount(0);
        }
        else {
            exchangeInfo.setAmount(exchangeDetails.getExchangeDifferenceAmount());
        }
        exchangeInfo.setProductIdToReplace(exchangeDetails.getReplacementProductId());
        exchangeInfo.setOrderId(order.getId());
        exchangeInfo.setPaymentMode(order.getPaymentMethod());
        exchangeInfo.setProductIdToPick(productExchangeDto.getNewProductId());
        return exchangeInfo;
    }

    // updating the exchange details in order class
    public ExchangeDetails updateExchangeDetails(Order order,ProductExchangeRequest productExchangeDto,double newPrice,double oldPrice){
        log.info("Updating the exchange details in order class");
        ExchangeDetails exchangeDetails = new ExchangeDetails();
        exchangeDetails.setReplacementProductId(productExchangeDto.getNewProductId());
        exchangeDetails.setReason(productExchangeDto.getReasonToReplace());
        exchangeDetails.setOriginalPrice( oldPrice );
        exchangeDetails.setReplacementPrice(newPrice);
        exchangeDetails.setCreatedAt(new Date());
        String payType = oldPrice > newPrice ? "REFUNDABLE":"PAYABLE";
        log.info("the Exchange is : "+payType);
        double exchangePrice = oldPrice-newPrice;
        exchangePrice = Math.abs(exchangePrice);
        if(exchangePrice == 0) {
            exchangeDetails.setExchangeType("NO_DIFFERENCE");
            exchangeDetails.setPaymentStatus("SUCCESS");
        }else{
            exchangeDetails.setExchangeType(payType);
        }
        if(payType.equalsIgnoreCase("REFUNDABLE")){
            exchangeDetails.setRefundMode("UPI");
            exchangeDetails.setRefundStatus("PENDING");
            order.setRefundAmount(roundToTwoDecimals(exchangePrice));
            Refund refund =  initiateRefundForExchange(order); // this initiate the refund for exchange.
            exchangeDetails.setRefundId(refund.getRefundId());
        }else{
            // customer as to pay the remaining amount.
            exchangeDetails.setPaymentMode(order.getPaymentMethod());
            exchangeDetails.setPaymentStatus("PENDING");
        }
        exchangeDetails.setExchangeDifferenceAmount(exchangePrice);
        order.setExchangeDetails(exchangeDetails);
        log.info("Exchange method is: "+payType);
        return exchangeDetails;
    }


    public OrderItem createNewOrderItem(Order order,ProductExchangeRequest productExchangeDto){
        log.info("updating the new product details in order class");
        double taxRate = getTaxForNewProduct(order.getAddressId(),productExchangeDto.getNewProductId());
        // Adding the replacement product to the order items
        OrderItem item = new OrderItem();
        item.setProductId(productExchangeDto.getNewProductId());
        item.setPrice(productService.getProductPrice(productExchangeDto.getNewProductId()) * productExchangeDto.getQuantity());
        item.setQuantity(productExchangeDto.getQuantity());
        item.setColor(productExchangeDto.getColor());
        item.setStatus("FOR_REPLACE");
        double tax = (item.getPrice() * taxRate) / 100;
        item.setTax(tax);
        System.out.println("new item: "+item);
        return item;
    }


    public double getTaxForNewProduct(String addressId,String productId){
        String shippingState = addressService.getAddressById(addressId).getState();
        Product product = productService.getProductById(productId);
        String rootCategoryId = categoryService.getRootCategoryId(product.getCategoryIds());
        double taxRate = taxRuleService.getApplicableTaxRate(rootCategoryId, shippingState);
        log.info("The tax rate for new product is: "+taxRate);
        return taxRate;
    }


    public double calculateNewFinalAmount(double oldPrice, double newPrice, double finalOrderAmount, double totalOriginalPrice, double totalDiscount) {

        double discountOnOldProduct = (oldPrice / totalOriginalPrice) * totalDiscount;
        double oldProductFinalAmount = oldPrice - discountOnOldProduct;
        double newFinalAmount = finalOrderAmount - oldProductFinalAmount + newPrice;
        return roundToTwoDecimals(newFinalAmount);
    }
    private  double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }


    public Refund initiateRefundForExchange(Order order){
        log.info("initiating the refund of exchange.");
        Refund refund  = new Refund();
        refund.setRefundId(String.valueOf(sequenceGeneratorService.getNextSequence("refundId")));
        refund.setUserId(order.getBuyerId());
        refund.setStatus("PENDING");
        refund.setRequestedAt(new Date());
        refund.setReason(order.getExchangeDetails().getReason());
        refund.setOrderId(order.getId());
        refund.setRefundAmount(order.getRefundAmount());
        refundService.saveRefund(refund);
        return refund;
    }


    public void processExchangeAfterUpiPayDone(String orderId, String paymentId){
        log.info("Process the exchange request after the payable amount is paid ");
        Order order = orderService.getOrder(orderId);
        ExchangeDetails exchangeDetails = order.getExchangeDetails();
        exchangeDetails.setPaymentStatus("SUCCESS");
        exchangeDetails.setPaymentId(paymentId);
        exchangeDetails.setUpdatedAt(new Date());
        for(OrderItem orderItem : order.getOrderItems()){
            if(orderItem.getStatus().equalsIgnoreCase("FOR_REPLACE")){
                updateNewProductStockToReplace(orderItem);
            }
        }
        orderService.saveOrder(order);
        assignDeliveryForExchange(order); // assigning the delivery after the payment done.
        ProductExchangeInfo productExchangeInfo = getExchangeInformation(order.getId());
        emailService.sendExchangeConfirmationEmail("iamanil3121@gmail.com",productExchangeInfo,order.getUpiId());
    }


    public void markExchangeCodPaymentSuccess(ExchangeUpdateRequest exchangeUpdateRequest){
        log.info("update the COD payment success.");
        Order order = orderService.getOrder(exchangeUpdateRequest.getOrderId());
        ExchangeDetails exchangeDetails = order.getExchangeDetails();
        exchangeDetails.setPaymentStatus("SUCCESS");
        exchangeDetails.setPaymentId(exchangeUpdateRequest.getPaymentId());
        exchangeDetails.setUpdatedAt(new Date());
        orderService.saveOrder(order);
    }

    // here we update the new product stock.
    public void updateNewProductStockToReplace(OrderItem item){
        log.info("updating the product stock that as to replace.");
        StockLogModificationRequest stockLogModificationDTO = new StockLogModificationRequest();
        stockLogModificationDTO.setAction("SOLD");
        stockLogModificationDTO.setModifiedAt(new Date());
        stockLogModificationDTO.setQuantityChanged(item.getQuantity());
        stockLogModificationDTO.setSellerId(productService.getProductById(item.getProductId()).getSellerId());
        stockLogModificationDTO.setProductId(item.getProductId());
        stockLogService.modifyStock(stockLogModificationDTO);

    }



    // Assigning the exchange to delivery person
    public DeliveryPerson assignDeliveryForExchange(Order order){
        log.info("Assigning the new products that as to replace to the delivery person.");
        DeliveryPerson deliveryPerson = deliveryService.getDeliveryPerson(shippingService.getByShippingId(order.getShippingId()).getDeliveryPersonId());
        if(deliveryPerson.getToExchangeItems().isEmpty()){
            deliveryPerson.setToExchangeItems(new ArrayList<>());
        }
        List<OrderItem> orderItems  = order.getOrderItems();
        ExchangeDeliveryItems exchangeDeliveryItems = new ExchangeDeliveryItems();
        for(OrderItem item : orderItems){
            if(item.getStatus().equalsIgnoreCase("REQUESTED_TO_RETURN")){
                exchangeDeliveryItems.setProductIdToPick(item.getProductId());
            }
            if(item.getStatus().equalsIgnoreCase("FOR_REPLACE")){
                exchangeDeliveryItems.setProductIdToReplace(item.getProductId());
            }
        }
        exchangeDeliveryItems.setAddress(addressService.getAddressById(order.getAddressId()));
        if(order.getPaymentMethod().equalsIgnoreCase("UPI")){
            exchangeDeliveryItems.setAmount(0.0);
            exchangeDeliveryItems.setPayable(false);
        }else{
            exchangeDeliveryItems.setAmount(order.getExchangeDetails().getExchangeDifferenceAmount());
            exchangeDeliveryItems.setPayable(true);
        }
        exchangeDeliveryItems.setOrderId(order.getId());
        exchangeDeliveryItems.setUserName(userService.getUserById(order.getBuyerId()).getName());
        exchangeDeliveryItems.setUserName(userService.getUserById(order.getBuyerId()).getName());
        exchangeDeliveryItems.setPaymentMode(order.getPaymentMethod());
        deliveryPerson.getToExchangeItems().add(exchangeDeliveryItems);
        deliveryPerson.setToDeliveryCount(deliveryPerson.getToDeliveryCount()+1);
        System.out.println("deliveryPerson in assign: "+deliveryPerson);
        // start with notifying the delivery person about the exchange.
        emailService.sendExchangeAssignmentMailToDeliveryPerson("iamanil3121@gmail.com",deliveryPerson,exchangeDeliveryItems);
        return  deliveryService.updateDeliveryPerson(deliveryPerson);
    }


    public ProductExchangeInfo getExchangeInformation(String orderId){
        log.info("Getting the Exchange information of order: "+orderId);
        Order order = orderService.getOrder(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        String productToReplace="",productId="";
        for(OrderItem item : orderItems){
            if(item.getStatus().equalsIgnoreCase("REQUESTED_TO_RETURN")){
                productToReplace = item.getProductId();
            }
            if(item.getStatus().equalsIgnoreCase("FOR_REPLACE")){
                productId = item.getProductId();
            }
        }
        DeliveryPerson deliveryPerson = deliveryService.getDeliveryPerson(shippingService.getByShippingId(order.getShippingId()).getDeliveryPersonId());
        System.out.println("delivery person:"+deliveryPerson);
        ProductExchangeInfo productExchangeResponse = new ProductExchangeInfo();
        productExchangeResponse.setOrderId(order.getId());
        productExchangeResponse.setProductIdToPick(productToReplace);
        productExchangeResponse.setProductIdToReplace(productId);
        productExchangeResponse.setAmountPayType(order.getExchangeDetails().getExchangeType());
        productExchangeResponse.setOrderPaymentType(order.getPaymentMethod());
        productExchangeResponse.setAmount(order.getExchangeDetails().getExchangeDifferenceAmount());
        productExchangeResponse.setExpectedReturnDate(calculateExpectedDate()); // just getting one sample expected date.
        productExchangeResponse.setDeliveryPersonId(deliveryPerson.getId());
        productExchangeResponse.setDeliveryPersonName(deliveryPerson.getName());
        productExchangeResponse.setPaymentStatus(order.getExchangeDetails().getPaymentStatus());

        return productExchangeResponse;
    }


    public Order updateExchangeSuccess(String orderId,String deliveryPersonId){
        log.info("Updating the Exchange status as success.");
        Order order = orderService.getOrder(orderId);
        updateStockAfterExchangeSuccess(orderId);// updating the stock of returned object.
        for(OrderItem orderItem : order.getOrderItems()){
            System.out.println("orderItem status:  "+orderItem.getStatus());
            if(orderItem.getStatus().equalsIgnoreCase("REQUESTED_TO_RETURN")){
                orderItem.setStatus("EXCHANGE_RETURNED");
            }
            if(orderItem.getStatus().equalsIgnoreCase("FOR_REPLACE")){
                orderItem.setStatus("EXCHANGE_DELIVERED");
            }
        }
        ShippingUpdateRequest shippingUpdateDTO = new ShippingUpdateRequest();
        shippingUpdateDTO.setShippingId(order.getShippingId());
        shippingUpdateDTO.setUpdateBy("ADMIN");
        shippingUpdateDTO.setNewValue("RETURNED");
        shippingService.updateShippingStatus(shippingUpdateDTO);
        deliveryService.removeExchangeItemFromDeliveryPerson(deliveryPersonId,orderId);
        order.getExchangeDetails().setUpdatedAt(new Date());
        order.setReturned(true);
        if(order.getExchangeDetails().getExchangeType().equalsIgnoreCase("REFUNDABLE")){
            completeRefundAfterExchangeSuccess(order.getRefundId()); // update the refund status after the exchange completed.
        }
        return orderService.saveOrder(order);
    }

    // here we update the stock of product replaced
    public void updateStockAfterExchangeSuccess(String orderID){
        log.info("update the stock product that is returned in exchange. ");
        Order order = orderService.getOrder(orderID);
        List<OrderItem> orderItems = order.getOrderItems();
        OrderItem orderItem = new OrderItem();
        for(OrderItem item : orderItems){
            if(item.getStatus().equalsIgnoreCase("REQUESTED_TO_RETURN")){
                orderItem = item;
            }
        }
        StockLogModificationRequest stockLogModificationDTO = new StockLogModificationRequest();
        stockLogModificationDTO.setAction("EXCHANGED");
        stockLogModificationDTO.setModifiedAt(new Date());
        stockLogModificationDTO.setQuantityChanged(orderItem.getQuantity());
        stockLogModificationDTO.setSellerId(productService.getProductById(orderItem.getProductId()).getSellerId());
        stockLogModificationDTO.setProductId(orderItem.getProductId());
        stockLogService.modifyStock(stockLogModificationDTO);

    }


    public void completeRefundAfterExchangeSuccess(String refundId){
        Refund refund = refundService.getRefundById(refundId);
        refund.setProcessedAt(new Date());
        refund.setStatus("COMPLETED");
        refundService.saveRefund(refund);
    }

    // called from the controller
    public void exchangeUpdate(ExchangeUpdateRequest exchangeUpdateRequest){
        ExchangeDetails exchangeDetails = orderService.getOrder(exchangeUpdateRequest.getOrderId()).getExchangeDetails();
        System.out.println("exchange: "+exchangeDetails);
        if(exchangeDetails.getExchangeType().equalsIgnoreCase("PAYABLE") && exchangeDetails.getPaymentMode().equalsIgnoreCase("COD") ){
            System.out.println("inside the if of update: "+exchangeUpdateRequest);
            PaymentRequest paymentDto = new PaymentRequest();
            paymentDto.setPaymentId(exchangeUpdateRequest.getPaymentId());
            paymentDto.setTransactionId(orderService.generateTransactionIdForCOD());
            paymentDto.setStatus("SUCCESS");
            paymentService.confirmCODPayment(paymentDto); // updating the payment success details
            markExchangeCodPaymentSuccess(exchangeUpdateRequest);// updating the order payment status
        }
    }


    private Date calculateExpectedDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        return cal.getTime();
    }
}
