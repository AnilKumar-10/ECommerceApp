package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.*;
import com.ECommerceApp.Model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ShippingDetails updateShippingStatusForRefundAndReturn(String orderId){

        ShippingUpdateDTO shippingUpdateDTO = new ShippingUpdateDTO();
        Order order = orderService.getOrder(orderId);
        shippingUpdateDTO.setShippingId(order.getShippingId());
        shippingUpdateDTO.setUpdateBy("ADMIN");
        shippingUpdateDTO.setNewValue("REQUESTED_TO_RETURNED");
        ShippingDetails  shippingDetails = shippingService.updateShippingStatus(shippingUpdateDTO);
        System.out.println("indide the return service class with : "+shippingDetails);
        return shippingDetails;
    }

    public DeliveryPerson assignReturnProductToDeliveryPerson(ShippingDetails shippingDetails,String reason){
        System.out.println("indide the return assignReturnProductToDeliveryPerson");
        DeliveryPerson deliveryPerson =  deliveryService.getDeliveryPerson(shippingDetails.getDeliveryPersonId());
        System.out.println("indide the return assignReturnProductToDeliveryPerson with delivery: "+deliveryPerson);
        ProductReturnDto productReturnDto = new ProductReturnDto();
        DeliveryItems deliveryItems = new DeliveryItems();
        for(DeliveryItems item : deliveryPerson.getToDeliveryItems()){
            if(shippingDetails.getId().equalsIgnoreCase(item.getShippingId())){
                productReturnDto.setProductPicked(false);
                productReturnDto.setAddress(item.getAddress());
                productReturnDto.setOrderId(item.getOrderId());
                productReturnDto.setUserName(item.getUserName());
                productReturnDto.setReason(reason);
            }
        }
        deliveryPerson.getToReturnItems().add(productReturnDto);
        System.out.println("indide the return service assignReturnProductToDeliveryPerson class wiht :"+deliveryPerson);
        return deliveryService.updateDeliveryPerson(deliveryPerson);
    }


    public RefundAndReturnResponseDTO getRefundAndReturnRepsonce(DeliveryPerson deliveryPerson, Refund refund1) {

        RefundAndReturnResponseDTO refundAndReturnResponseDTO = new RefundAndReturnResponseDTO();
        BeanUtils.copyProperties(refund1,refundAndReturnResponseDTO);
        refundAndReturnResponseDTO.setDeliveryPersonName(deliveryPerson.getName());
        refundAndReturnResponseDTO.setProductPicked(false);
        refundAndReturnResponseDTO.setExpectedPickUpDate(getExpectedDate(refund1.getRequestedAt()));

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

    private void updateOrderItemsForReturnSuccess(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        for(OrderItem orderItem:orderItems){
            if(orderItem.getStatus().equalsIgnoreCase("REQUEST_TO_RETURN")){
                orderItem.setStatus("RETURNED");
            }
        }
        order.setOrderItems(orderItems);
        orderService.saveOrder(order);
    }


    public void updateReturnFailed(String orderId){
        ShippingUpdateDTO shippingUpdateDTO = new ShippingUpdateDTO();
        Order order = orderService.getOrder(orderId);
        shippingUpdateDTO.setShippingId(order.getShippingId());
        shippingUpdateDTO.setUpdateBy("ADMIN");
        shippingUpdateDTO.setNewValue("RETURNED_FAILED");
        shippingService.updateShippingStatus(shippingUpdateDTO);
//        System.out.println("inside the return service class with : "+shippingDetails);
        updateStockLogAfterReturn(orderId); // updating the stock log after order returned.
    }


    public void updateStockLogAfterReturn(String orderId){
        StockLogModificationDTO stockLogModificationDTO = new StockLogModificationDTO();
        Order order = orderService.getOrder(orderId);
        OrderItem orderedProduct = order.getOrderItems().getFirst();
        stockLogModificationDTO.setAction("RETURNED");
        stockLogModificationDTO.setModifiedAt(new Date());
        stockLogModificationDTO.setQuantityChanged(orderedProduct.getQuantity());
        stockLogModificationDTO.setSellerId(productService.getProductById(orderedProduct.getProductId()).getSellerId());
        stockLogModificationDTO.setProductId(orderedProduct.getProductId());
        stockLogService.modifyStock(stockLogModificationDTO);
    }

    public void updateOrderItemsForReturn(List<OrderItem> orderItems, RaiseRefundRequestDto refundRequestDto) {
        Order order = orderService.getOrder(refundRequestDto.getOrderId());
        for(OrderItem orderItem : orderItems){
            if(refundRequestDto.getProductIds().contains(orderItem.getProductId())){
                orderItem.setStatus("REQUEST_T0_RETURN");
            }
        }
        order.setOrderItems(orderItems);
        orderService.saveOrder(order);
    }
}
