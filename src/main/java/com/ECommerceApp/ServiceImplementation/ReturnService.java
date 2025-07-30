package com.ECommerceApp.ServiceImplementation;

import com.ECommerceApp.DTO.Order.ShippingUpdateRequest;
import com.ECommerceApp.DTO.ReturnAndExchange.*;
import com.ECommerceApp.DTO.Product.StockLogModificationRequest;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Delivery.ShippingDetails;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Model.Order.OrderItem;
import com.ECommerceApp.Model.Product.StockLogModification;
import com.ECommerceApp.Model.RefundAndExchange.Refund;
import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.ServiceInterface.IReturnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ECommerceApp.ServiceInterface.*;

import java.util.*;
@Slf4j
@Service
public class ReturnService  implements IReturnService {

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
    private IPaymentService paymentService;

    public ShippingDetails updateShippingStatusForRefundAndReturn(String orderId){
        log.info("Updating the shipping details of the returning product");
        ShippingUpdateRequest shippingUpdateDTO = new ShippingUpdateRequest();
        Order order = orderService.getOrder(orderId);
        shippingUpdateDTO.setShippingId(order.getShippingId());
        shippingUpdateDTO.setUpdateBy(Users.Role.ADMIN.name());
        shippingUpdateDTO.setNewValue(Order.OrderStatus.REQUESTED_TO_RETURN);
        return shippingService.updateShippingStatus(shippingUpdateDTO);
    }

    public DeliveryPerson assignReturnProductToDeliveryPerson(ShippingDetails shippingDetails, String reason){
        log.info("Assigning the to return products to the delivery agent");
        DeliveryPerson deliveryPerson =  deliveryService.getDeliveryPerson(shippingDetails.getDeliveryPersonId());
        Order order = orderService.getOrder(shippingDetails.getOrderId());
        ProductReturnDetails productReturnDto = new ProductReturnDetails();
        productReturnDto.setProductPicked(false);
        productReturnDto.setReason(reason);
        productReturnDto.setOrderId(order.getId());
        productReturnDto.setUserName(userService.getUserById(order.getBuyerId()).getName());
        productReturnDto.setAddress(addressService.getAddressById(shippingDetails.getDeliveryAddress().getId()));
        log.info("Adding the products that need to be returned to the delivery agent.");
        for(OrderItem orderItem : order.getOrderItems()){
            if(Objects.equals(orderItem.getStatus(), Order.OrderStatus.REQUESTED_TO_RETURN.name())){

                productReturnDto.getProductsId().add(orderItem.getProductId());
                productReturnDto.getProductsName().add(orderItem.getName());

            }
        }
        deliveryPerson.getToReturnItems().add(productReturnDto);
        // here we have to send the product return details to the delivery person.
        emailService.sendReturnProductNotificationMail("iamanil3121@gmail.com",deliveryPerson,productReturnDto,order.getBuyerId());
        return deliveryService.updateDeliveryPerson(deliveryPerson);
    }


    public RefundAndReturnResponse getRefundAndReturnResponce(DeliveryPerson deliveryPerson, Refund refund1) {
        log.info("getting the return and refund details");
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
        return calendar.getTime();
    }

    public void updateReturnSuccess(String orderId){
        log.info("Updating the return status after the product picked safely.");
        Order order = orderService.getOrder(orderId);
        ShippingUpdateRequest shippingUpdateDTO = new ShippingUpdateRequest();
        shippingUpdateDTO.setShippingId(order.getShippingId());
        shippingUpdateDTO.setUpdateBy(Users.Role.ADMIN.name());
        shippingUpdateDTO.setNewValue(Order.OrderStatus.RETURNED);
        shippingService.updateShippingStatus(shippingUpdateDTO);
        updateOrderItemsForReturnSuccess(order);
        updateStockLogAfterReturn(orderId); // updating the stock log after order returned.

    }

    //this will update the status success of the orderItems product.
    private void updateOrderItemsForReturnSuccess(Order order) {
        log.info("updating the status to returned to the products that are requested.");
        List<OrderItem> orderItems = order.getOrderItems();
        for(OrderItem orderItem:orderItems){
            if(orderItem.getStatus().equals(Order.OrderStatus.REQUESTED_TO_RETURN.name()) ){
                orderItem.setStatus(Order.OrderStatus.RETURNED.name());
            }
        }
        order.setOrderItems(orderItems);
        orderService.saveOrder(order);
    }


    public void updateReturnFailed(String orderId){
        log.info("Updating the return failed.");
        ShippingUpdateRequest shippingUpdateDTO = new ShippingUpdateRequest();
        Order order = orderService.getOrder(orderId);
        order.setReturned(false);
        orderService.saveOrder(order);
        shippingUpdateDTO.setShippingId(order.getShippingId());
        shippingUpdateDTO.setUpdateBy(Users.Role.ADMIN.name());
        shippingUpdateDTO.setNewValue(Order.OrderStatus.RETURN_FAILED);
        shippingService.updateShippingStatus(shippingUpdateDTO);
    }


    public void updateStockLogAfterReturn(String orderId){
        log.info("updating the stock log after the product returned.");
        Order order = orderService.getOrder(orderId);
        List<OrderItem> orderedProducts = order.getOrderItems();
        for(OrderItem orderItem : orderedProducts){
            if(orderItem.getStatus().equals(Order.OrderStatus.RETURNED.name())){
                StockLogModificationRequest stockLogModificationDTO = new StockLogModificationRequest();
                stockLogModificationDTO.setAction(StockLogModification.ActionType.RETURNED);
                stockLogModificationDTO.setModifiedAt(new Date());
                stockLogModificationDTO.setQuantityChanged(orderItem.getQuantity());
                stockLogModificationDTO.setSellerId(productService.getProductById(orderItem.getProductId()).getSellerId());
                stockLogModificationDTO.setProductId(orderItem.getProductId());
                stockLogService.modifyStock(stockLogModificationDTO);
            }
        }
    }


    public void updateStockLogAfterOrderCancellation(String orderId){
        log.info("update stock log after the order cancellation.");
        Order order = orderService.getOrder(orderId);
        List<OrderItem> orderedProducts = order.getOrderItems();
        for(OrderItem orderItem : orderedProducts){
            if(orderItem.getStatus()==null){
                StockLogModificationRequest stockLogModificationDTO = new StockLogModificationRequest();
                stockLogModificationDTO.setAction(StockLogModification.ActionType.CANCELLED);
                stockLogModificationDTO.setModifiedAt(new Date());
                stockLogModificationDTO.setQuantityChanged(orderItem.getQuantity());
                stockLogModificationDTO.setSellerId(productService.getProductById(orderItem.getProductId()).getSellerId());
                stockLogModificationDTO.setProductId(orderItem.getProductId());
                stockLogService.modifyStock(stockLogModificationDTO);
            }
        }
    }

    public void updateOrderItemsForReturn(List<OrderItem> orderItems, RaiseRefundRequest refundRequestDto) {
        log.info("updating the product status to REQUESTED_TO_RETURN after the return approved. ");
        Order order = orderService.getOrder(refundRequestDto.getOrderId());
        for(OrderItem orderItem : orderItems){
            if(refundRequestDto.getProductIds().contains(orderItem.getProductId())){
                orderItem.setStatus(Order.OrderStatus.REQUESTED_TO_RETURN.name());
            }
        }
        order.setOrderItems(orderItems);
        orderService.saveOrder(order);
    }

}

