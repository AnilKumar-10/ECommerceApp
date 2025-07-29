package com.ECommerceApp.ServiceImplementation;

import com.ECommerceApp.DTO.Delivery.DeliveryUpdate;
import com.ECommerceApp.DTO.Order.ShippingUpdateRequest;
import com.ECommerceApp.Exceptions.Delivery.DeliveryNotFoundException;
import com.ECommerceApp.Exceptions.Order.ShippingDetailsNotFoundException;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Delivery.ModificationLog;
import com.ECommerceApp.Model.Delivery.ShippingDetails;
import com.ECommerceApp.Model.Order.*;
import com.ECommerceApp.Repository.DeliveryRepository;
import com.ECommerceApp.Repository.OrderRepository;
import com.ECommerceApp.Repository.ShippingRepository;
import com.ECommerceApp.ServiceInterface.IShippingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ECommerceApp.ServiceInterface.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class ShippingService implements IShippingService {

    @Autowired
    private ShippingRepository shippingRepo;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    private IDeliveryService deliveryService;
    @Autowired
    private IAddressService addressService;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private IDeliveryHistoryService deliveryHistoryService;
    @Autowired
    private OrderShippingMediatorService mediatorService;



    public ShippingDetails createShippingDetails(Order order) {
        log.info("Creating the shipping details for the order: "+order.getId());
        ShippingDetails shipping = new ShippingDetails();
        long nextId = sequenceGeneratorService.getNextSequence("shippingId");
        shipping.setId(String.valueOf(nextId));
        shipping.setOrderId(order.getId());
        shipping.setCourierName(getCourierName());  // this will set some random courier name for every order
        shipping.setDeliveryAddress(addressService.getAddressById(order.getAddressId()));
        shipping.setStatus(Order.OrderStatus.PLACED);
        shipping.setTrackingId(generateTrackingId());
        shipping.setExpectedDate(calculateExpectedDate());
        shipping.setModificationLogs(new ArrayList<>());

        // Assign delivery person based on area
        String address  = order.getAddressId();

        DeliveryPerson assigned = deliveryService.assignDeliveryPerson(order.getAddressId());

        System.out.println("inside the service with: "+assigned);
        if (assigned != null) {
            shipping.setDeliveryPersonId(assigned.getId());
            assigned.setToDeliveryCount(assigned.getToDeliveryCount() + 1);
            deliveryService.save(assigned);
        }
        else{
            throw new DeliveryNotFoundException("There is no delivery available for the selected location..!!");
        }
        // Log creation
        ModificationLog log = new ModificationLog();
        log.setField("STATUS");
        log.setUpdatedBy(order.getBuyerId());
        log.setNewValue(Order.OrderStatus.PLACED);
        addLog(shipping,log);

        ShippingDetails shippingDetails = shippingRepo.save(shipping);
        order.setShippingId(shippingDetails.getId()); // updating the order with shipping details
        Order ord = mediatorService.saveOrder(order);
        deliveryService.assignProductsToDelivery(assigned.getId(), ord);
        return shippingDetails;
    }

    // 2. Update shipping status
    public ShippingDetails updateShippingStatus(ShippingUpdateRequest shippingUpdateDTO) {
        log.info("Updating the shipping status on every stage, present new status is: "+shippingUpdateDTO.getNewValue());
        System.out.println("inside update shipping stats: "+shippingUpdateDTO);
        ShippingDetails shipping = shippingRepo.findById(shippingUpdateDTO.getShippingId())
                .orElseThrow(() -> new ShippingDetailsNotFoundException("Shipping record not found"));
        Order order = mediatorService.getOrder(shipping.getOrderId());
        Order.OrderStatus oldStatus = shipping.getStatus();
        Order.OrderStatus newStatus = shippingUpdateDTO.getNewValue();
        if (!Objects.equals(oldStatus, newStatus) && !Order.OrderStatus.CANCELLED.equals(order.getOrderStatus())) {
            shipping.setStatus(newStatus);
            log.info("inside if: "+newStatus);
            order.setOrderStatus(newStatus); //here the order status is not updating in the db.
            log.info("inside if order: "+order.getOrderStatus());
            ModificationLog log = new ModificationLog();
            log.setField("STATUS");
            log.setUpdatedBy(shippingUpdateDTO.getUpdateBy());
            log.setOldValue(oldStatus);
            log.setNewValue(newStatus);
            addLog(shipping,log);
        }
        mediatorService.saveOrder(order);
        return shippingRepo.save(shipping);
    }


    // 5. Get shipping by order ID
    public ShippingDetails getShippingByOrderId(String orderId) {
        log.info("getting the shipping details with order: "+orderId);
        return shippingRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ShippingDetailsNotFoundException("Shipping not found for order ID"));
    }

    // 6. Get all shipping records for a delivery person
    public List<ShippingDetails> getByDeliveryPersonId(String deliveryPersonId) {
        return shippingRepo.findByDeliveryPersonId(deliveryPersonId);
    }

    // 7. Private: Add a flat modification log entry
    // trace all the changes of the order after placing the order.
    private void addLog(ShippingDetails shipping,ModificationLog modificationLog) {

        log.info("updating the shipping modification");
        if (shipping.getModificationLogs() == null)
            shipping.setModificationLogs(new ArrayList<>());

        modificationLog.setModifiedAt(new Date());
        shipping.getModificationLogs().add(modificationLog);
    }


    // 9. Private: Calculate expected delivery date (+5 days)
    private Date calculateExpectedDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        return cal.getTime();
    }


    public String updateDeliveryStatus(DeliveryUpdate deliveryUpdateDTO){
        log.info("Updating the delivery status after successful delivery.");
        ShippingUpdateRequest shippingUpdateDTO  = new ShippingUpdateRequest();
        shippingUpdateDTO.setUpdateBy(deliveryUpdateDTO.getUpdateBy());
        shippingUpdateDTO.setShippingId(deliveryUpdateDTO.getShippingId());
        shippingUpdateDTO.setNewValue(deliveryUpdateDTO.getNewValue());
        updateShippingStatus(shippingUpdateDTO); // to update the shipping status to delivered
        Order order = updateOrderItemsDeliveredStatus(shippingUpdateDTO); //  to update the orderItems status to delivered.
        String deliveryPersonId = shippingRepo.findById(deliveryUpdateDTO.getShippingId()).get().getDeliveryPersonId();
        deliveryService.updateDeliveryCount(deliveryPersonId); // updates the count
//      here we have to update the delivery history
        deliveryHistoryService.insertDelivery(order.getId(),deliveryPersonId);
        // removes the order details from to deliver list
        deliveryService.removeDeliveredOrderFromToDeliveryItems(deliveryPersonId,deliveryUpdateDTO.getOrderId());
        emailService.sendOrderDeliveredEmail("iamanil3121@gmail.com","Anil",order);
        return "Your order is delivered successfully please rate us..!";
    }


    public ShippingDetails getByShippingId(String shippingId){
        return shippingRepo.findById(shippingId).get();
    }

    public String generateTrackingId() {
        return "TRK-" + UUID.randomUUID().toString().toUpperCase().replaceAll("-", "").substring(0, 10);
    }

    // this updates the status of the ordered items delivered when the order is delivered.
    public  Order updateOrderItemsDeliveredStatus(ShippingUpdateRequest shippingUpdateDTO){
        log.info("updating the product status to delivered after the delivery success.");
        ShippingDetails shippingDetails = shippingRepo.findById(shippingUpdateDTO.getShippingId()).get();
        Order order = mediatorService.getOrder(shippingDetails.getOrderId());
        List<OrderItem> orderItems = new ArrayList<>();
        if(shippingUpdateDTO.getNewValue()== Order.OrderStatus.DELIVERED){
            orderItems = order.getOrderItems();
        }
        for(OrderItem item : orderItems){
            item.setStatus(Order.OrderStatus.DELIVERED.name());
        }
        order.setOrderItems(orderItems);
        return mediatorService.saveOrder(order);
    }


    public String getCourierName(){
        List<String> courierNames = List.of(
                "Ekart Logistics",
                "Amazon Transportation Services",
                "Delhivery",
                "Shadowfax",
                "XpressBees"
        );
        return courierNames.get(ThreadLocalRandom.current().nextInt(courierNames.size()));
    }





}
