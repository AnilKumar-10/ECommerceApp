package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.*;
import com.ECommerceApp.Model.DeliveryPerson;
import com.ECommerceApp.Model.Order;
import com.ECommerceApp.Model.Refund;
import com.ECommerceApp.Model.ShippingDetails;
import com.ECommerceApp.Repository.DeliveryRepository;
import com.ECommerceApp.Repository.RefundRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

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
    private DeliveryRepository  deliveryRepository;
    @Autowired
    private RefundRepository refundRepository;
    @Autowired
    private RefundService refundService;

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
        return deliveryRepository.save(deliveryPerson);
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

    public Refund updateReturnSuccess(String orderId){
        ShippingUpdateDTO shippingUpdateDTO = new ShippingUpdateDTO();
        Order order = orderService.getOrder(orderId);
        shippingUpdateDTO.setShippingId(order.getShippingId());
        shippingUpdateDTO.setUpdateBy("ADMIN");
        shippingUpdateDTO.setNewValue("RETURNED");
        ShippingDetails  shippingDetails = shippingService.updateShippingStatus(shippingUpdateDTO);
        System.out.println("indide the return service class with : "+shippingDetails);
        Refund refund = refundRepository.findByOrderId(orderId);
        return refundService.completeRefund(refund.getRefundId());
    }

}
