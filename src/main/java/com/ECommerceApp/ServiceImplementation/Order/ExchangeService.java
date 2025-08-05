package com.ECommerceApp.ServiceImplementation.Order;

import com.ECommerceApp.DTO.Order.ShippingUpdateRequest;
import com.ECommerceApp.DTO.Payment.PaymentRequest;
import com.ECommerceApp.DTO.Product.StockLogModificationRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.*;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Order.*;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.Model.Product.StockLogModification;
import com.ECommerceApp.Model.RefundAndExchange.Refund;
import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.ServiceInterface.Delivery.IDeliveryService;
import com.ECommerceApp.ServiceInterface.Order.*;
import com.ECommerceApp.ServiceInterface.Payment.IPaymentService;
import com.ECommerceApp.ServiceInterface.Product.ICategoryService;
import com.ECommerceApp.ServiceInterface.Product.IProductService;
import com.ECommerceApp.ServiceInterface.Product.IStockLogService;
import com.ECommerceApp.ServiceInterface.User.IAddressService;
import com.ECommerceApp.ServiceInterface.User.IEmailService;
import com.ECommerceApp.ServiceInterface.User.UserServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
public class ExchangeService  implements IExchangeService {

    @Autowired
    private IOrderService orderService;
    @Autowired
    private UserServiceInterface userService;
    @Autowired
    private IShippingService shippingService;
    @Autowired
    private IDeliveryService deliveryService;
    @Autowired
    private IProductService productService;
    @Autowired
    private IStockLogService stockLogService;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private IAddressService addressService;
    @Autowired
    private ITaxRuleService taxRuleService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    private IRefundService refundService;
    @Autowired
    private IPaymentService paymentService;


    public ExchangeResponse exchangeRequest(ProductExchangeRequest productExchangeDto){
        log.info("Request to exchange.");
        Order order = orderService.getOrder(productExchangeDto.getOrderId());
        System.out.println("in exchange method; "+order);
        double oldPrice=0;
        double oldTax=0;
        for(OrderItem orderItem : order.getOrderItems()){
            if(orderItem.getProductId().equalsIgnoreCase(productExchangeDto.getProductIdToReplace())){
                orderItem.setStatus(Order.OrderStatus.REQUESTED_TO_EXCHANGE.name());
                oldTax = orderItem.getTax();
                oldPrice = orderItem.getPrice()+oldTax;
            }
        }

        // Adding the replacement product to the order items
        OrderItem item = createNewOrderItem(order,productExchangeDto);
        double newTax = item.getTax();
        order.setTax(order.getTax() - oldTax + newTax);
        order.getOrderItems().add(item);
        double newPrice = item.getPrice()+newTax;
        double finalAmount = calculateNewFinalAmount(oldPrice, newPrice, order.getFinalAmount(), order.getTotalAmount(), order.getDiscount());
        order.setTotalAmount(order.getTotalAmount() - oldPrice + newPrice);
        order.setFinalAmount(finalAmount - oldTax + newTax);
        log.info("old price: "+oldPrice+"  , new price; "+newPrice+"  ,new final price: "+finalAmount+ ",  new total price: "+order.getTotalAmount());

        // Updating the exchange details in the order class.
        ExchangeDetails exchangeDetails = updateExchangeDetails(order, productExchangeDto, newPrice, oldPrice);
        orderService.saveOrder(order);
        // assign the exchange delivery person.
        if (
                exchangeDetails.getExchangeType() == ExchangeDetails.ExchangeType.NO_DIFFERENCE ||
                        order.getPaymentMethod() == Payment.PaymentMethod.COD ||
                        exchangeDetails.getExchangeType() == ExchangeDetails.ExchangeType.REFUNDABLE
        ){
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
        shippingUpdateDTO.setUpdateBy(Users.Role.ADMIN.name());
        shippingUpdateDTO.setNewValue(Order.OrderStatus.REQUESTED_TO_EXCHANGE);
        shippingUpdateDTO.setShippingId(order.getShippingId());
        shippingService.updateShippingStatus(shippingUpdateDTO); // this updates the shipping status for exchange.

        return getExchangeInfo(productExchangeDto, exchangeDetails, order);
    }

    // this will get the exchange information
    private ExchangeResponse getExchangeInfo(ProductExchangeRequest productExchangeDto, ExchangeDetails exchangeDetails, Order order) {
        log.info("Getting the exchange response");
        ExchangeResponse exchangeInfo = new ExchangeResponse();
        exchangeInfo.setAmountPayType(exchangeDetails.getExchangeType().name());
        if(exchangeDetails.getExchangeType()== ExchangeDetails.ExchangeType.NO_DIFFERENCE){
            exchangeInfo.setAmount(0);
        }
        else {
            exchangeInfo.setAmount(exchangeDetails.getExchangeDifferenceAmount());
        }
        exchangeInfo.setProductIdToReplace(exchangeDetails.getReplacementProductId());
        exchangeInfo.setOrderId(order.getId());
        exchangeInfo.setPaymentMode(order.getPaymentMethod().name());
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
        ExchangeDetails.ExchangeType payType = oldPrice > newPrice ? ExchangeDetails.ExchangeType.REFUNDABLE : ExchangeDetails.ExchangeType.PAYABLE;

        log.info("the Exchange is : "+payType);
        double exchangePrice = oldPrice-newPrice;
        exchangePrice = Math.abs(exchangePrice);
        if(exchangePrice == 0) {
            exchangeDetails.setExchangeType(ExchangeDetails.ExchangeType.NO_DIFFERENCE);
            exchangeDetails.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        }else{
            exchangeDetails.setExchangeType(payType);
        }
        if (payType == ExchangeDetails.ExchangeType.REFUNDABLE) {
            exchangeDetails.setRefundMode(ExchangeDetails.RefundMode.UPI);
            exchangeDetails.setRefundStatus(Refund.RefundStatus.PENDING); // assuming you have a RefundStatus enum
            order.setRefundAmount(roundToTwoDecimals(exchangePrice));

            Refund refund = initiateRefundForExchange(order); // this initiates the refund
            exchangeDetails.setRefundId(refund.getRefundId());

        } else {
            // customer has to pay the remaining amount
            exchangeDetails.setPaymentMode(order.getPaymentMethod());
            exchangeDetails.setPaymentStatus(Payment.PaymentStatus.PENDING); // assuming you have a PaymentStatus enum
        }

        exchangeDetails.setExchangeDifferenceAmount(exchangePrice);
        order.setExchangeDetails(exchangeDetails);
        log.info("Exchange method is: "+payType);
        return exchangeDetails;
    }
// change the enums in the exchange details.

    public OrderItem createNewOrderItem(Order order,ProductExchangeRequest productExchangeDto){
        log.info("updating the new product details in order class");
        double taxRate = getTaxForNewProduct(order.getAddressId(),productExchangeDto.getNewProductId());
        // Adding the replacement product to the order items
        OrderItem item = new OrderItem();
        item.setProductId(productExchangeDto.getNewProductId());
        item.setPrice(productService.getProductPrice(productExchangeDto.getNewProductId()) * productExchangeDto.getQuantity());
        item.setQuantity(productExchangeDto.getQuantity());
        item.setColor(productExchangeDto.getColor());
        item.setStatus(Order.OrderStatus.TO_DELIVER.name());
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
        refund.setStatus(Refund.RefundStatus.PENDING);
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
        exchangeDetails.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        exchangeDetails.setPaymentId(paymentId);
        exchangeDetails.setUpdatedAt(new Date());
        for(OrderItem orderItem : order.getOrderItems()){
            if(orderItem.getStatus().equalsIgnoreCase(Order.OrderStatus.TO_DELIVER.name())){
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
        exchangeDetails.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        exchangeDetails.setPaymentId(exchangeUpdateRequest.getPaymentId());
        exchangeDetails.setUpdatedAt(new Date());
        orderService.saveOrder(order);
    }

    // here we update the new product stock.
    public void updateNewProductStockToReplace(OrderItem item){
        log.info("updating the product stock that as to replace.");
        StockLogModificationRequest stockLogModificationDTO = new StockLogModificationRequest();
        stockLogModificationDTO.setAction(StockLogModification.ActionType.SOLD);
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
            if(item.getStatus().equalsIgnoreCase(Order.OrderStatus.REQUESTED_TO_RETURN.name())){
                exchangeDeliveryItems.setProductIdToPick(item.getProductId());
            }
            if(item.getStatus().equalsIgnoreCase(Order.OrderStatus.TO_DELIVER.name())){
                exchangeDeliveryItems.setProductIdToReplace(item.getProductId());
            }
        }
        exchangeDeliveryItems.setAddress(addressService.getAddressById(order.getAddressId()));
        if(order.getPaymentMethod() == Payment.PaymentMethod.UPI){
            exchangeDeliveryItems.setAmount(0.0);
            exchangeDeliveryItems.setPayable(false);
        }else{
            exchangeDeliveryItems.setAmount(order.getExchangeDetails().getExchangeDifferenceAmount());
            exchangeDeliveryItems.setPayable(true);
        }
        exchangeDeliveryItems.setOrderId(order.getId());
        exchangeDeliveryItems.setUserName(userService.getUserById(order.getBuyerId()).getName());
        exchangeDeliveryItems.setUserName(userService.getUserById(order.getBuyerId()).getName());
        exchangeDeliveryItems.setPaymentMode(order.getPaymentMethod().name());
        deliveryPerson.getToExchangeItems().add(exchangeDeliveryItems);
        deliveryPerson.setToDeliveryCount(deliveryPerson.getToDeliveryCount()+1);
        log.info("deliveryPerson in assigned: "+deliveryPerson);
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
            if(item.getStatus().equalsIgnoreCase(Order.OrderStatus.REQUESTED_TO_RETURN.name())){
                productToReplace = item.getProductId();
            }
            if(item.getStatus().equalsIgnoreCase(Order.OrderStatus.TO_DELIVER.name())){
                productId = item.getProductId();
            }
        }
        DeliveryPerson deliveryPerson = deliveryService.getDeliveryPerson(shippingService.getByShippingId(order.getShippingId()).getDeliveryPersonId());
        ProductExchangeInfo productExchangeResponse = new ProductExchangeInfo();
        productExchangeResponse.setOrderId(order.getId());
        productExchangeResponse.setProductIdToPick(productToReplace);
        productExchangeResponse.setProductIdToReplace(productId);
        productExchangeResponse.setAmountPayType(order.getExchangeDetails().getExchangeType().name());
        productExchangeResponse.setOrderPaymentType(order.getPaymentMethod().name());
        productExchangeResponse.setAmount(order.getExchangeDetails().getExchangeDifferenceAmount());
        productExchangeResponse.setExpectedReturnDate(calculateExpectedDate()); // just getting one sample expected date.
        productExchangeResponse.setDeliveryPersonId(deliveryPerson.getId());
        productExchangeResponse.setDeliveryPersonName(deliveryPerson.getName());
        productExchangeResponse.setPaymentStatus(order.getExchangeDetails().getPaymentStatus().name());

        return productExchangeResponse;
    }


    public Order updateExchangeSuccess(String orderId,String deliveryPersonId){
        log.info("Updating the Exchange status as success.");
        Order order = orderService.getOrder(orderId);
        updateStockAfterExchangeSuccess(orderId);// updating the stock of returned object.
        for(OrderItem orderItem : order.getOrderItems()){
            System.out.println("orderItem status:  "+orderItem.getStatus());
            if(orderItem.getStatus().equalsIgnoreCase(Order.OrderStatus.REQUESTED_TO_RETURN.name())){
                orderItem.setStatus(Order.OrderStatus.EXCHANGE_RETURNED.name());
            }
            if(orderItem.getStatus().equalsIgnoreCase(Order.OrderStatus.TO_DELIVER.name())){
                orderItem.setStatus(Order.OrderStatus.EXCHANGE_DELIVERED.name());
            }
        }
        ShippingUpdateRequest shippingUpdateDTO = new ShippingUpdateRequest();
        shippingUpdateDTO.setShippingId(order.getShippingId());
        shippingUpdateDTO.setUpdateBy(Users.Role.ADMIN.name());
        shippingUpdateDTO.setNewValue(Order.OrderStatus.EXCHANGED);
        shippingService.updateShippingStatus(shippingUpdateDTO);
        deliveryService.removeExchangeItemFromDeliveryPerson(deliveryPersonId,orderId);
        order.getExchangeDetails().setUpdatedAt(new Date());
        order.setReturned(true);
        if(order.getExchangeDetails().getExchangeType() == ExchangeDetails.ExchangeType.REFUNDABLE){
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
            if(item.getStatus().equalsIgnoreCase(Order.OrderStatus.REQUESTED_TO_RETURN.name())){
                orderItem = item;
            }
        }
        StockLogModificationRequest stockLogModificationDTO = new StockLogModificationRequest();
        stockLogModificationDTO.setAction(StockLogModification.ActionType.RETURNED);
        stockLogModificationDTO.setModifiedAt(new Date());
        stockLogModificationDTO.setQuantityChanged(orderItem.getQuantity());
        stockLogModificationDTO.setSellerId(productService.getProductById(orderItem.getProductId()).getSellerId());
        stockLogModificationDTO.setProductId(orderItem.getProductId());
        stockLogService.modifyStock(stockLogModificationDTO);

    }


    public void completeRefundAfterExchangeSuccess(String refundId){
        Refund refund = refundService.getRefundById(refundId);
        refund.setProcessedAt(new Date());
        refund.setStatus(Refund.RefundStatus.COMPLETED);
        refundService.saveRefund(refund);
    }

    // called from the controller
    public void exchangeUpdate(ExchangeUpdateRequest exchangeUpdateRequest){
        ExchangeDetails exchangeDetails = orderService.getOrder(exchangeUpdateRequest.getOrderId()).getExchangeDetails();
        if(exchangeDetails.getExchangeType()== ExchangeDetails.ExchangeType.PAYABLE && exchangeDetails.getPaymentMode() == Payment.PaymentMethod.COD ){
            PaymentRequest paymentDto = new PaymentRequest();
            paymentDto.setPaymentId(exchangeUpdateRequest.getPaymentId());
            paymentDto.setTransactionId(orderService.generateTransactionIdForCOD());
            paymentDto.setStatus(Payment.PaymentStatus.SUCCESS);
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
