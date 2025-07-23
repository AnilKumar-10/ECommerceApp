package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.Order.ShippingUpdateRequest;
import com.ECommerceApp.DTO.Product.StockLogModificationRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.*;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Order.Order;
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

    public ExchangeInfo exchangeRequest(ProductExchangeRequest productExchangeDto){
        log.info("Request to exchange.");
        Order order = orderService.getOrder(productExchangeDto.getOrderId());
        System.out.println("in exchange method; "+order);
        double oldPrice=0;
        for(OrderItem orderItem : order.getOrderItems()){
            if(orderItem.getProductId().equalsIgnoreCase(productExchangeDto.getProductIdToReplace())){
                orderItem.setStatus("REQUESTED_TO_REPLACE");
                oldPrice = orderItem.getPrice()+orderItem.getTax();
//                updateStockAfterReplace(orderItem);
            }
        }
        String shippingState = addressService.getAddressById(order.getAddressId()).getState();
        Product product = productService.getProductById(productExchangeDto.getProductIdToReplace());
        String rootCategoryId = categoryService.getRootCategoryId(product.getCategoryIds());
        double taxRate = taxRuleService.getApplicableTaxRate(rootCategoryId, shippingState);

        // Adding the replacement product to the order items
        OrderItem item = new OrderItem();
        item.setProductId(productExchangeDto.getProductId());
        item.setPrice(productService.getProductPrice(productExchangeDto.getProductId()) * productExchangeDto.getQuantity());
        item.setQuantity(productExchangeDto.getQuantity());
        item.setColor(productExchangeDto.getColor());
        item.setStatus("FOR_REPLACE");
        double tax = (item.getPrice() * taxRate) / 100;
        item.setTax(tax);
        System.out.println("new item: "+item);
        order.getOrderItems().add(item);
        double newPrice = item.getPrice() + tax;
        double finalAmount = oldPrice - newPrice;
        log.info("original price: "+oldPrice+"  , new price; "+newPrice+"  , final price: "+finalAmount);
        order.setFinalAmount(order.getFinalAmount() + finalAmount);

        // Adding the exchange details to the order class.
        ExchangeDetails exchangeDetails = new ExchangeDetails();
        exchangeDetails.setReplacementProductId(productExchangeDto.getProductId());
        exchangeDetails.setReason(productExchangeDto.getReasonToReplace());
        exchangeDetails.setPaymentMode(order.getPaymentMethod());
        exchangeDetails.setPaymentStatus("PENDING");
        exchangeDetails.setOriginalPrice( oldPrice );
        exchangeDetails.setReplacementPrice(newPrice);
        exchangeDetails.setRefundMode("UPI");
        exchangeDetails.setRefundStatus("PENDING");
        exchangeDetails.setCreatedAt(new Date());
        String payType = oldPrice > newPrice ? "REFUNDABLE":"PAYABLE";
        if(oldPrice-newPrice == 0){
            exchangeDetails.setExchangeType("NO_DIFFERENCE");
        }
        if(payType.equalsIgnoreCase("REFUNDABLE")){
            order.setRefundAmount(finalAmount);
            initiateRefundForExchange(order); // this initiate the refund for exchange.

        }
        exchangeDetails.setExchangeType(payType);
        exchangeDetails.setExchangeDifferenceAmount(finalAmount);
        order.setExchangeDetails(exchangeDetails);
        System.out.println("exchange details: "+exchangeDetails);

        // assign the exchange delivery person.
        if(exchangeDetails.getExchangeType().equalsIgnoreCase("NO_DIFFERENCE") || order.getPaymentMethod().equalsIgnoreCase("COD") || exchangeDetails.getExchangeType().equalsIgnoreCase("REFUNDABLE") ){
            DeliveryPerson deliveryPerson =  assignDeliveryForExchange(order);
            updateNewProductStockToReplace(item); // this will update stock of new product.
            if (exchangeDetails.getExchangeType().equalsIgnoreCase("NO_DIFFERENCE")) {
                order.getExchangeDetails().setPaymentMode("No payment needed");
                order.getExchangeDetails().setPaymentStatus("SUCCESS");
            }
            System.out.println("delivery Person:"+deliveryPerson);
        }
        log.info("the Exchange is : "+payType);
//        log.info("Update the shipping status to REQUEST_TO_EXCHANGE");
        ShippingUpdateRequest shippingUpdateDTO = new ShippingUpdateRequest();
        shippingUpdateDTO.setUpdateBy("ADMIN");
        shippingUpdateDTO.setNewValue("REQUEST_TO_EXCHANGE");
        shippingUpdateDTO.setShippingId(order.getShippingId());
        shippingService.updateShippingStatus(shippingUpdateDTO); // this updates the shipping status for exchange.
        ExchangeInfo exchangeInfo = new ExchangeInfo();
        exchangeInfo.setAmount(finalAmount);
        exchangeInfo.setAmountPayType(exchangeDetails.getExchangeType());
        exchangeInfo.setProductIdToReplace(exchangeDetails.getReplacementProductId());
        exchangeInfo.setOrderId(order.getId());
        exchangeInfo.setPaymentMode(order.getPaymentMethod());
        exchangeInfo.setProductIdToPick(productExchangeDto.getProductId());
        orderService.saveOrder(order);
        return exchangeInfo;
    }


    public ProductExchangeResponse processExchangeAfterUpiPayDone(String orderId){
        log.info("Process the exchange request after the payable amount is paid ");
        Order order = orderService.getOrder(orderId);
        ExchangeDetails exchangeDetails = order.getExchangeDetails();
        exchangeDetails.setPaymentStatus("SUCCESS");
        for(OrderItem orderItem : order.getOrderItems()){
            if(orderItem.getStatus().equalsIgnoreCase("FOR_REPLACE")){
                updateNewProductStockToReplace(orderItem);
            }
        }
        orderService.saveOrder(order);
        assignDeliveryForExchange(order); // assigning the delivery after the payment done.
        return getExchangeInformation(orderId);
    }


    public void markExchangeCodPaymentSuccess(String orderId,String paymentId){
        log.info("update the COD payment success.");
        Order order = orderService.getOrder(orderId);
        ExchangeDetails exchangeDetails = order.getExchangeDetails();
        exchangeDetails.setPaymentStatus("SUCCESS");
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

    // here we update the stock of product replaced
    public void updateStockAfterExchangeSuccess(String orderID){
        log.info("update the stock product that is returned in exchange. ");
        Order order = orderService.getOrder(orderID);
        List<OrderItem> orderItems = order.getOrderItems();
        OrderItem orderItem = new OrderItem();
        for(OrderItem item : orderItems){
            if(item.getStatus().equalsIgnoreCase("REQUESTED_TO_REPLACE")){
                orderItem = item;
            }
        }
        StockLogModificationRequest stockLogModificationDTO = new StockLogModificationRequest();
        stockLogModificationDTO.setAction("RETURNED");
        stockLogModificationDTO.setModifiedAt(new Date());
        stockLogModificationDTO.setQuantityChanged(orderItem.getQuantity());
        stockLogModificationDTO.setSellerId(productService.getProductById(orderItem.getProductId()).getSellerId());
        stockLogModificationDTO.setProductId(orderItem.getProductId());
        stockLogService.modifyStock(stockLogModificationDTO);

    }


    public DeliveryPerson assignDeliveryForExchange(Order order){
        log.info("Assigning the new products that as to replace to the delivery person.");
        DeliveryPerson deliveryPerson = deliveryService.getDeliveryPerson(shippingService.getByShippingId(order.getShippingId()).getDeliveryPersonId());
        if(deliveryPerson.getToExchangeItems().isEmpty()){
            deliveryPerson.setToExchangeItems(new ArrayList<>());
        }
        List<OrderItem> orderItems  = order.getOrderItems();
        ExchangeDeliveryItems exchangeDeliveryItems = new ExchangeDeliveryItems();
        for(OrderItem item : orderItems){
            if(item.getStatus().equalsIgnoreCase("REQUESTED_TO_REPLACE")){
                exchangeDeliveryItems.setProductIdToPick(item.getProductId());
            }
            if(item.getStatus().equalsIgnoreCase("FOR_REPLACE")){
                exchangeDeliveryItems.setProductIdToReplace(item.getProductId());
            }
        }
        exchangeDeliveryItems.setAddress(addressService.getAddressById(order.getAddressId()));
        if(order.getPaymentMethod().equalsIgnoreCase("UPI")){
            exchangeDeliveryItems.setAmount(0.0);
        }else{
            exchangeDeliveryItems.setAmount(order.getExchangeDetails().getReplacementPrice());
        }
        exchangeDeliveryItems.setOrderId(order.getId());
        exchangeDeliveryItems.setUserName(userService.getUserById(order.getBuyerId()).getName());
        deliveryPerson.getToExchangeItems().add(exchangeDeliveryItems);
        deliveryPerson.setToDeliveryCount(deliveryPerson.getToDeliveryCount()+1);
        System.out.println("deliveryPerson in assign: "+deliveryPerson);
        return  deliveryService.updateDeliveryPerson(deliveryPerson);
    }



    public ProductExchangeResponse getExchangeInformation(String orderId){
        log.info("Getting the Exchange information of order: "+orderId);
        Order order = orderService.getOrder(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        String productToReplace="",productId="";
        for(OrderItem item : orderItems){
            if(item.getStatus().equalsIgnoreCase("REQUESTED_TO_REPLACE")){
                productToReplace = item.getProductId();
            }
            if(item.getStatus().equalsIgnoreCase("FOR_REPLACE")){
                productId = item.getProductId();
            }
        }
        DeliveryPerson deliveryPerson = deliveryService.getDeliveryPerson(shippingService.getByShippingId(order.getShippingId()).getDeliveryPersonId());
        System.out.println("delivery person:"+deliveryPerson);
        ProductExchangeResponse productExchangeResponse = new ProductExchangeResponse();
        productExchangeResponse.setOrderId(order.getId());
        productExchangeResponse.setProductIdToPick(productToReplace);
        productExchangeResponse.setProductIdToReplace(productId);
        productExchangeResponse.setAmountPayType(order.getExchangeDetails().getExchangeType());
        productExchangeResponse.setAmount(order.getExchangeDetails().getExchangeDifferenceAmount());
        productExchangeResponse.setExpectedReturnDate(calculateExpectedDate());
        productExchangeResponse.setDeliveryPersonId(deliveryPerson.getId());
        productExchangeResponse.setDeliveryPersonName(deliveryPerson.getName());
        return productExchangeResponse;
    }



    public Order updateExchangeSuccess(String orderId,String deliveryPersonId){
        log.info("Updating the Exchange status as success.");
        Order order = orderService.getOrder(orderId);
        updateStockAfterExchangeSuccess(orderId);// updating the stock of returned object.
        for(OrderItem orderItem : order.getOrderItems()){
            if(orderItem.getStatus().equalsIgnoreCase("REQUESTED_TO_REPLACE")){
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
        completeRefundForExchange(order.getRefundId()); // update the refund status after the exchange completed.
        return orderService.saveOrder(order);
    }


    public void initiateRefundForExchange(Order order){
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
    }

    public void completeRefundForExchange(String refundId){
        Refund refund = refundService.getRefundById(refundId);
        refund.setProcessedAt(new Date());
        refund.setStatus("COMPLETED");
        refundService.saveRefund(refund);
    }

    private Date calculateExpectedDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        return cal.getTime();
    }
}
