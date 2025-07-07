package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.DeliveryUpdateDTO;
import com.ECommerceApp.DTO.ShippingUpdateDTO;
import com.ECommerceApp.Exceptions.DeliveryNotFoundException;
import com.ECommerceApp.Model.DeliveryPerson;
import com.ECommerceApp.Model.ModificationLog;
import com.ECommerceApp.Model.Order;
import com.ECommerceApp.Model.ShippingDetails;
import com.ECommerceApp.Repository.DeliveryRepository;
import com.ECommerceApp.Repository.OrderRepository;
import com.ECommerceApp.Repository.ShippingRepository;
import com.mongodb.annotations.Alpha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShippingService {

    @Autowired
    private ShippingRepository shippingRepo;
    @Autowired
    private  DeliveryRepository deliveryRepo;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AddressService addressService;


    public ShippingDetails createShippingDetails(Order order) {
        ShippingDetails shipping = new ShippingDetails();
        long nextId = sequenceGeneratorService.getNextSequence("shippingId");
        shipping.setId(String.valueOf(nextId));
        shipping.setOrderId(order.getId());
        shipping.setDeliveryAddress(addressService.getAddressById(order.getAddressId()));
        shipping.setStatus("PLACED");
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
            deliveryRepo.save(assigned);
        }
        else{
            throw new DeliveryNotFoundException("There is no delivery for the selection location..!!");
        }
        // Log creation
        addLog(shipping, "status", null, "PLACED", order.getBuyerId());
        ShippingDetails shippingDetails = shippingRepo.save(shipping);
//        Order orderd  = orderService.getOrder(order.getId());
        order.setShippingId(shippingDetails.getId()); // updating the order with shipping details
        orderRepository.save(order);
        return shippingDetails;
    }

    // 2. Update shipping status
    public ShippingDetails updateShippingStatus(ShippingUpdateDTO shippingUpdateDTO) {
        ShippingDetails shipping = shippingRepo.findById(shippingUpdateDTO.getShippingId())
                .orElseThrow(() -> new RuntimeException("Shipping record not found"));

        String oldStatus = shipping.getStatus();
        if (!Objects.equals(oldStatus, shippingUpdateDTO.getNewValue())) {
            shipping.setStatus(shippingUpdateDTO.getNewValue());
            addLog(shipping, "status", oldStatus, shippingUpdateDTO.getNewValue(), shippingUpdateDTO.getUpdateBy());
        }

        return shippingRepo.save(shipping);
    }

    // 3. Update courier name or tracking ID
    public ShippingDetails updateCourierDetails(ShippingUpdateDTO shippingUpdateDTO) {
        ShippingDetails shipping = shippingRepo.findById(shippingUpdateDTO.getShippingId())
                .orElseThrow(() -> new RuntimeException("Shipping record not found"));

        if (shippingUpdateDTO.getNewValue() != null && !Objects.equals(shippingUpdateDTO.getNewValue(), shipping.getCourierName())) {
            addLog(shipping, "courierName", shipping.getCourierName(), shippingUpdateDTO.getNewValue(), shippingUpdateDTO.getUpdateBy());
            shipping.setCourierName(shippingUpdateDTO.getNewValue());
        }

//        if (trackingId != null && !Objects.equals(trackingId, shipping.getTrackingId())) {
//            addLog(shipping, "trackingId", shipping.getTrackingId(), trackingId, updatedBy);
//            shipping.setTrackingId(trackingId);
//        }

        return shippingRepo.save(shipping);
    }

//    // 4. Update expected delivery date
//    public ShippingDetails updateExpectedDate(ShippingUpdateDTO shippingUpdateDTO) {
//        ShippingDetails shipping = shippingRepo.findById(shippingUpdateDTO.getShippingId())
//                .orElseThrow(() -> new RuntimeException("Shipping not found"));
//
//        Date oldDate = shipping.getExpectedDate();
//        if (!Objects.equals(oldDate, shippingUpdateDTO.ge)) {
//            addLog(shipping, "expectedDate",
//                    oldDate != null ? oldDate.toString() : null,
//                    newDate.toString(), updatedBy);
//
//            shipping.setExpectedDate(newDate);
//        }
//
//        return shippingRepo.save(shipping);
//    }

    // 5. Get shipping by order ID
    public ShippingDetails getByOrderId(String orderId) {
        return shippingRepo.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Shipping not found for order ID"));
    }

    // 6. Get all shipping records for a delivery person
    public List<ShippingDetails> getByDeliveryPersonId(String deliveryPersonId) {
        return shippingRepo.findByDeliveryPersonId(deliveryPersonId);
    }

    // 7. Private: Add a flat modification log entry
    private void addLog(ShippingDetails shipping, String field, String oldVal, String newVal, String updatedBy) {
        if (shipping.getModificationLogs() == null)
            shipping.setModificationLogs(new ArrayList<>());

        ModificationLog log = new ModificationLog();
        log.setField(field);
        log.setUpdatedBy(updatedBy);
        log.setOldValue(oldVal);
        log.setNewValue(newVal);
        log.setModifiedAt(new Date());

        shipping.getModificationLogs().add(log);
    }


    // 9. Private: Calculate expected delivery date (+5 days)
    private Date calculateExpectedDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        return cal.getTime();
    }


    public String updateDeliveryStatus(DeliveryUpdateDTO deliveryUpdateDTO){
        ShippingUpdateDTO shippingUpdateDTO  = new ShippingUpdateDTO();
        shippingUpdateDTO.setUpdateBy(deliveryUpdateDTO.getUpdateBy());
        shippingUpdateDTO.setShippingId(deliveryUpdateDTO.getShippingId());
        shippingUpdateDTO.setNewValue(deliveryUpdateDTO.getNewValue());
        updateShippingStatus(shippingUpdateDTO);

        return "Your order is delivered successfully please rate us..!";
    }


    public String generateTrackingId() {
        return "TRK-" + UUID.randomUUID().toString().toUpperCase().replaceAll("-", "").substring(0, 10);
    }

}
