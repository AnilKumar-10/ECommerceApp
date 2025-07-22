package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.*;
import com.ECommerceApp.DTO.StockLogModification;
import com.ECommerceApp.Model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ReturnService {

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
    private PaymentService paymentService;

    public ShippingDetails updateShippingStatusForRefundAndReturn(String orderId){

        ShippingUpdateRequest shippingUpdateDTO = new ShippingUpdateRequest();
        Order order = orderService.getOrder(orderId);
        shippingUpdateDTO.setShippingId(order.getShippingId());
        shippingUpdateDTO.setUpdateBy("ADMIN");
        shippingUpdateDTO.setNewValue("REQUESTED_TO_RETURN");
        ShippingDetails  shippingDetails = shippingService.updateShippingStatus(shippingUpdateDTO);
//        System.out.println("indide the return service class with : "+shippingDetails);
        return shippingDetails;
    }

    public DeliveryPerson assignReturnProductToDeliveryPerson(ShippingDetails shippingDetails,String reason){
        System.out.println("indide the return assignReturnProductToDeliveryPerson");
        DeliveryPerson deliveryPerson =  deliveryService.getDeliveryPerson(shippingDetails.getDeliveryPersonId());
        System.out.println("indide the return assignReturnProductToDeliveryPerson with delivery: "+deliveryPerson);
        Order order = orderService.getOrder(shippingDetails.getOrderId());
        ProductReturnRequest productReturnDto = new ProductReturnRequest();
        productReturnDto.setProductPicked(false);
        productReturnDto.setReason(reason);
        productReturnDto.setOrderId(order.getId());
        productReturnDto.setUserName(userService.getUserById(order.getBuyerId()).getName());
        productReturnDto.setAddress(addressService.getAddressById(shippingDetails.getDeliveryAddress().getId()));
        for(OrderItem orderItem : order.getOrderItems()){
            if(orderItem.getStatus().equalsIgnoreCase("REQUESTED_TO_RETURN")){
                productReturnDto.getProductsId().add(orderItem.getProductId());
                productReturnDto.getProductsName().add(orderItem.getName());

            }
        }
        deliveryPerson.getToReturnItems().add(productReturnDto);
        System.out.println("indide the return service assignReturnProductToDeliveryPerson class with :"+deliveryPerson);
        // here we have to send the product return details to the delivery person.
        emailService.sendReturnProductNotificationMail("iamanil3121@gmail.com",deliveryPerson,productReturnDto,order.getBuyerId());
        return deliveryService.updateDeliveryPerson(deliveryPerson);
    }


    public RefundAndReturnResponse getRefundAndReturnResponce(DeliveryPerson deliveryPerson, Refund refund1) {

        RefundAndReturnResponse refundAndReturnResponseDTO = new RefundAndReturnResponse();
        BeanUtils.copyProperties(refund1,refundAndReturnResponseDTO);
        refundAndReturnResponseDTO.setDeliveryPersonName(deliveryPerson.getName());
        refundAndReturnResponseDTO.setProductPicked(false);
        refundAndReturnResponseDTO.setExpectedPickUpDate(getExpectedDate(refund1.getRequestedAt()));
        emailService.sendReturnRequestedEmail("iamanil3121@gmail.com",refundAndReturnResponseDTO);
        return refundAndReturnResponseDTO;
    }

    public Date getExpectedDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);  // Set the calendar to your original date
        calendar.add(Calendar.DAY_OF_MONTH, 3);  // Add 3 days

        Date dateAfter3Days = calendar.getTime();  // Resulting date
        return dateAfter3Days;
    }

    public void updateReturnSuccess(String orderId){
        Order order = orderService.getOrder(orderId);
        ShippingUpdateRequest shippingUpdateDTO = new ShippingUpdateRequest();
        shippingUpdateDTO.setShippingId(order.getShippingId());
        shippingUpdateDTO.setUpdateBy("ADMIN");
        shippingUpdateDTO.setNewValue("RETURNED");
        shippingService.updateShippingStatus(shippingUpdateDTO);
        updateOrderItemsForReturnSuccess(order);
//        System.out.println("inside the return service class with : "+shippingDetails);
        updateStockLogAfterReturn(orderId); // updating the stock log after order returned.

    }

    //this will update the status success of the orderItems product.
    private void updateOrderItemsForReturnSuccess(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
//        System.out.println("inside updateorderitems: "+order);
//        System.out.println("inside updateorderitems before : "+orderItems);
        for(OrderItem orderItem:orderItems){
//            System.out.println("inside for with orderIten: "+orderItem.getStatus());
            if(orderItem.getStatus().equals("REQUESTED_TO_RETURN") ){
                orderItem.setStatus("RETURNED");
            }
        }
        order.setOrderItems(orderItems);
//        System.out.println("inside order after: "+orderItems);
        orderService.saveOrder(order);
    }


    public void updateReturnFailed(String orderId){
        ShippingUpdateRequest shippingUpdateDTO = new ShippingUpdateRequest();
        Order order = orderService.getOrder(orderId);
        shippingUpdateDTO.setShippingId(order.getShippingId());
        shippingUpdateDTO.setUpdateBy("ADMIN");
        shippingUpdateDTO.setNewValue("RETURNED_FAILED");
        shippingService.updateShippingStatus(shippingUpdateDTO);
    }


    public void updateStockLogAfterReturn(String orderId){
        Order order = orderService.getOrder(orderId);
        List<OrderItem> orderedProducts = order.getOrderItems();
        for(OrderItem orderItem : orderedProducts){
            if(orderItem.getStatus().equals("RETURNED")){
                StockLogModification stockLogModificationDTO = new StockLogModification();
                stockLogModificationDTO.setAction("RETURNED");
                stockLogModificationDTO.setModifiedAt(new Date());
                stockLogModificationDTO.setQuantityChanged(orderItem.getQuantity());
                stockLogModificationDTO.setSellerId(productService.getProductById(orderItem.getProductId()).getSellerId());
                stockLogModificationDTO.setProductId(orderItem.getProductId());
                stockLogService.modifyStock(stockLogModificationDTO);
            }
        }
    }


    public void updateStockLogAfterOrderCancellation(String orderId){
        Order order = orderService.getOrder(orderId);
        List<OrderItem> orderedProducts = order.getOrderItems();
        for(OrderItem orderItem : orderedProducts){
            if(orderItem.getStatus()==null){
                StockLogModification stockLogModificationDTO = new StockLogModification();
                stockLogModificationDTO.setAction("CANCELLED");
                stockLogModificationDTO.setModifiedAt(new Date());
                stockLogModificationDTO.setQuantityChanged(orderItem.getQuantity());
                stockLogModificationDTO.setSellerId(productService.getProductById(orderItem.getProductId()).getSellerId());
                stockLogModificationDTO.setProductId(orderItem.getProductId());
                stockLogService.modifyStock(stockLogModificationDTO);
            }
        }
    }

    public void updateOrderItemsForReturn(List<OrderItem> orderItems, RaiseRefundRequest refundRequestDto) {
        Order order = orderService.getOrder(refundRequestDto.getOrderId());
        for(OrderItem orderItem : orderItems){
            if(refundRequestDto.getProductIds().contains(orderItem.getProductId())){
                orderItem.setStatus("REQUESTED_TO_RETURN");
            }
        }
        order.setOrderItems(orderItems);
        orderService.saveOrder(order);
    }


    public ExchangeInfo exchangeRequest(ProductExchangeRequest productExchangeDto){
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
        order.setFinalAmount(order.getFinalAmount() + finalAmount);
        // Adding the exchange details to the order class.
        ExchangeDetails exchangeDetails = new ExchangeDetails();
        exchangeDetails.setReplacementProductId(productExchangeDto.getProductId());
        exchangeDetails.setReason(productExchangeDto.getReasonToReplace());
        exchangeDetails.setPaymentMode(order.getPaymentMethod());
        exchangeDetails.setPaymentStatus("PENDING");
        exchangeDetails.setOriginalPrice( oldPrice );
        exchangeDetails.setReplacementPrice(newPrice);
        exchangeDetails.setCreatedAt(new Date());
        String payType = oldPrice > newPrice ? "REFUNDABLE":"PAYABLE";
        if(oldPrice-newPrice == 0){
            exchangeDetails.setExchangeType("NO_DIFFERENCE");
        }
        if(payType.equalsIgnoreCase("REFUNDABLE")){
            order.setRefundAmount(finalAmount);
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
        Order order = orderService.getOrder(orderId);
        ExchangeDetails exchangeDetails = order.getExchangeDetails();
        exchangeDetails.setPaymentStatus("SUCCESS");
        orderService.saveOrder(order);
    }

    // here we update the new product stock.
    public void updateNewProductStockToReplace(OrderItem item){
        StockLogModification stockLogModificationDTO = new StockLogModification();
        stockLogModificationDTO.setAction("SOLD");
        stockLogModificationDTO.setModifiedAt(new Date());
        stockLogModificationDTO.setQuantityChanged(item.getQuantity());
        stockLogModificationDTO.setSellerId(productService.getProductById(item.getProductId()).getSellerId());
        stockLogModificationDTO.setProductId(item.getProductId());
        stockLogService.modifyStock(stockLogModificationDTO);

    }

    // here we update the stock of product replaced
    public void updateStockAfterExchangeSuccess(String orderID){
             Order order = orderService.getOrder(orderID);
             List<OrderItem> orderItems = order.getOrderItems();
             OrderItem orderItem = new OrderItem();
             for(OrderItem item : orderItems){
                 if(item.getStatus().equalsIgnoreCase("REQUESTED_TO_REPLACE")){
                     orderItem = item;
                 }
             }
        StockLogModification stockLogModificationDTO = new StockLogModification();
        stockLogModificationDTO.setAction("RETURNED");
        stockLogModificationDTO.setModifiedAt(new Date());
        stockLogModificationDTO.setQuantityChanged(orderItem.getQuantity());
        stockLogModificationDTO.setSellerId(productService.getProductById(orderItem.getProductId()).getSellerId());
        stockLogModificationDTO.setProductId(orderItem.getProductId());
        stockLogService.modifyStock(stockLogModificationDTO);

    }


    public DeliveryPerson assignDeliveryForExchange(Order order){
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
        return orderService.saveOrder(order);
    }



    private Date calculateExpectedDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        return cal.getTime();
    }
}

