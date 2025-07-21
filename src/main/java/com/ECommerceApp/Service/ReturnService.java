package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.*;
import com.ECommerceApp.Model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.stereotype.Service;

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

    public ShippingDetails updateShippingStatusForRefundAndReturn(String orderId){

        ShippingUpdateDTO shippingUpdateDTO = new ShippingUpdateDTO();
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
        ProductReturnDto productReturnDto = new ProductReturnDto();
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


    public RefundAndReturnResponseDTO getRefundAndReturnResponce(DeliveryPerson deliveryPerson, Refund refund1) {

        RefundAndReturnResponseDTO refundAndReturnResponseDTO = new RefundAndReturnResponseDTO();
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
        ShippingUpdateDTO shippingUpdateDTO = new ShippingUpdateDTO();
        Order order = orderService.getOrder(orderId);
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
            if(orderItem.getStatus().equals("REQUESTED_TO_RETURN")){
                orderItem.setStatus("RETURNED");
            }
        }
        order.setOrderItems(orderItems);
//        System.out.println("inside order after: "+orderItems);
        orderService.saveOrder(order);
    }


    public void updateReturnFailed(String orderId){
        ShippingUpdateDTO shippingUpdateDTO = new ShippingUpdateDTO();
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
                StockLogModificationDTO stockLogModificationDTO = new StockLogModificationDTO();
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
                StockLogModificationDTO stockLogModificationDTO = new StockLogModificationDTO();
                stockLogModificationDTO.setAction("CANCELLED");
                stockLogModificationDTO.setModifiedAt(new Date());
                stockLogModificationDTO.setQuantityChanged(orderItem.getQuantity());
                stockLogModificationDTO.setSellerId(productService.getProductById(orderItem.getProductId()).getSellerId());
                stockLogModificationDTO.setProductId(orderItem.getProductId());
                stockLogService.modifyStock(stockLogModificationDTO);
            }
        }
    }

    public void updateOrderItemsForReturn(List<OrderItem> orderItems, RaiseRefundRequestDto refundRequestDto) {
        Order order = orderService.getOrder(refundRequestDto.getOrderId());
        for(OrderItem orderItem : orderItems){
            if(refundRequestDto.getProductIds().contains(orderItem.getProductId())){
                orderItem.setStatus("REQUESTED_TO_RETURN");
            }
        }
        order.setOrderItems(orderItems);
        orderService.saveOrder(order);
    }


    public ProductExchangeResponse exchangeRequest(ProductExchangeRequest productExchangeDto){
        Order order = orderService.getOrder(productExchangeDto.getOrderId());
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

        OrderItem item = new OrderItem();
        item.setProductId(productExchangeDto.getProductId());
        item.setPrice(productService.getProductPrice(productExchangeDto.getProductId())*productExchangeDto.getQuantity());
        item.setQuantity(productExchangeDto.getQuantity());
        item.setColor(productExchangeDto.getColor());
        item.setStatus("FOR_REPLACE");
        double tax = (item.getPrice() * taxRate) / 100;
        item.setTax(tax);
        order.getOrderItems().add(item);
        double newPrice =item.getPrice()+item.getTax();
        updateStockToReplace(item); // this will update stock of replaced product.
        double finalAmount = oldPrice - newPrice;

        ExchangeDetails exchangeDetails = new ExchangeDetails();
        exchangeDetails.setReplacementProductId(productExchangeDto.getProductId());
        exchangeDetails.setReason(productExchangeDto.getReasonToReplace());
        exchangeDetails.setPaymentMode(order.getPaymentMethod());
        exchangeDetails.setPaymentStatus("PENDING");
        exchangeDetails.setOriginalPrice( oldPrice );
        exchangeDetails.setReplacementPrice(newPrice);
        exchangeDetails.setExchangeDifferenceAmount(finalAmount);
        order.setExchangeDetails(exchangeDetails);
        Order order1 = orderService.saveOrder(order);

        // assign the exchange delivery person.
        DeliveryPerson deliveryPerson = assignDeliveryForExchange(order1);
        ProductExchangeResponse productExchangeResponse = new ProductExchangeResponse();
        productExchangeResponse.setOrderId(order.getId());
        productExchangeResponse.setProductIdToPick(productExchangeDto.getProductId());
        productExchangeResponse.setProductIdToReplace(productExchangeDto.getProductIdToReplace());
        productExchangeResponse.setAmount(order.getExchangeDetails().getExchangeDifferenceAmount());
        productExchangeResponse.setExpectedReturnDate(calculateExpectedDate());
        productExchangeResponse.setDeliveryPersonId(deliveryPerson.getId());
        productExchangeResponse.setDeliveryPersonName(deliveryPerson.getName());
        return productExchangeResponse;

    }

    private void updateStockAfterReplace(OrderItem item) {
        StockLogModificationDTO stockLogModificationDTO = new StockLogModificationDTO();
        stockLogModificationDTO.setAction("RETURN");
        stockLogModificationDTO.setModifiedAt(new Date());
        stockLogModificationDTO.setQuantityChanged(item.getQuantity());
        stockLogModificationDTO.setSellerId(productService.getProductById(item.getProductId()).getSellerId());
        stockLogModificationDTO.setProductId(item.getProductId());
        stockLogService.modifyStock(stockLogModificationDTO);

    }

    public void updateStockToReplace(OrderItem item){
        StockLogModificationDTO stockLogModificationDTO = new StockLogModificationDTO();
        stockLogModificationDTO.setAction("SOLD");
        stockLogModificationDTO.setModifiedAt(new Date());
        stockLogModificationDTO.setQuantityChanged(item.getQuantity());
        stockLogModificationDTO.setSellerId(productService.getProductById(item.getProductId()).getSellerId());
        stockLogModificationDTO.setProductId(item.getProductId());
        stockLogService.modifyStock(stockLogModificationDTO);

    }

    public void updateStockExchangeSuccess(){

    }


    public DeliveryPerson assignDeliveryForExchange(Order order){
        DeliveryPerson deliveryPerson = deliveryService.getDeliveryPerson(shippingService.getByShippingId(order.getShippingId()).getDeliveryPersonId());
        List<OrderItem> orderItems  = order.getOrderItems();
        ExchangeDeliveryItems exchangeDeliveryItems = new ExchangeDeliveryItems();
        for(OrderItem item : orderItems){
            if(item.getStatus().equalsIgnoreCase("REQUESTED_TO_REPLACE")){
                exchangeDeliveryItems.setProductIdToPick(item.getProductId());
            }
            else if(item.getStatus().equalsIgnoreCase("REPLACE")){
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
        return  deliveryService.updateDeliveryPerson(deliveryPerson);
    }



    private Date calculateExpectedDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        return cal.getTime();
    }
}

