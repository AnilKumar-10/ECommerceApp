package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.DeliveryUpdateDTO;
import com.ECommerceApp.DTO.ShippingUpdateDTO;
import com.ECommerceApp.Exceptions.DeliveryNotFoundException;
import com.ECommerceApp.Exceptions.ShippingDetailsNotFoundException;
import com.ECommerceApp.Model.*;
import com.ECommerceApp.Repository.DeliveryRepository;
import com.ECommerceApp.Repository.OrderRepository;
import com.ECommerceApp.Repository.ShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
    @Autowired
    private EmailService emailService;

    public ShippingDetails createShippingDetails(Order order) {
        ShippingDetails shipping = new ShippingDetails();
        long nextId = sequenceGeneratorService.getNextSequence("shippingId");
        shipping.setId(String.valueOf(nextId));
        shipping.setOrderId(order.getId());
        shipping.setCourierName(getCourierName());  // this will set some random courier name for every order
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
            throw new DeliveryNotFoundException("There is no delivery available for the selected location..!!");
        }
        // Log creation
        addLog(shipping, "status", null, "PLACED", order.getBuyerId());
        ShippingDetails shippingDetails = shippingRepo.save(shipping);
        order.setShippingId(shippingDetails.getId()); // updating the order with shipping details
        Order ord = orderRepository.save(order);
        deliveryService.assignProductsToDelivery(assigned.getId(), ord);
        return shippingDetails;
    }

    // 2. Update shipping status
    public ShippingDetails updateShippingStatus(ShippingUpdateDTO shippingUpdateDTO) {
        System.out.println("inside update shipping stats: "+shippingUpdateDTO);
        ShippingDetails shipping = shippingRepo.findById(shippingUpdateDTO.getShippingId())
                .orElseThrow(() -> new RuntimeException("Shipping record not found"));
        Order order = orderRepository.findById(shipping.getOrderId()).get();
        System.out.println("order:"+order);
        String oldStatus = shipping.getStatus();
        String newValue = shippingUpdateDTO.getNewValue();
        if (!Objects.equals(oldStatus, newValue) &&
                (!"REQUESTED_TO_RETURN".equals(oldStatus) || "RETURNED".equals(newValue))) {
            shipping.setStatus(shippingUpdateDTO.getNewValue());
            order.setOrderStatus(shippingUpdateDTO.getNewValue());
            addLog(shipping, "status", oldStatus, shippingUpdateDTO.getNewValue(), shippingUpdateDTO.getUpdateBy());
        }
        orderRepository.save(order);
        return shippingRepo.save(shipping);
    }

    // 3. Update courier name or tracking ID
    public ShippingDetails updateCourierDetails(ShippingUpdateDTO shippingUpdateDTO) {
        ShippingDetails shipping = shippingRepo.findById(shippingUpdateDTO.getShippingId())
                .orElseThrow(() -> new ShippingDetailsNotFoundException("Shipping record not found"));

        if (shippingUpdateDTO.getNewValue() != null && !Objects.equals(shippingUpdateDTO.getNewValue(), shipping.getCourierName())) {
            addLog(shipping, "courierName", shipping.getCourierName(), shippingUpdateDTO.getNewValue(), shippingUpdateDTO.getUpdateBy());
            shipping.setCourierName(shippingUpdateDTO.getNewValue());
        }

        return shippingRepo.save(shipping);
    }



    // 5. Get shipping by order ID
    public ShippingDetails getShippingByOrderId(String orderId) {
        return shippingRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ShippingDetailsNotFoundException("Shipping not found for order ID"));
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
        updateShippingStatus(shippingUpdateDTO); // to update the shipping status to delivered
        Order order = updateOrderItemsDeliveredStatus(shippingUpdateDTO); //  to update the orderItems status to delivered.
        String deliveryPersonId = shippingRepo.findById(deliveryUpdateDTO.getShippingId()).get().getDeliveryPersonId();
        deliveryService.updateDeliveryCount(deliveryPersonId); // updates the count
        deliveryService.removeDeliveredOrderFromToDeliveryItems(deliveryPersonId,deliveryUpdateDTO.getOrderId());
        // removes the order details from to deliver list
        emailService.sendOrderDeliveredEmail("sohailibrahim11223@gmail.com","Sohail",order);
        return "Your order is delivered successfully please rate us..!";
    }



    public ShippingDetails getByShippingId(String shippingId){
        return shippingRepo.findById(shippingId).get();
    }

    public String generateTrackingId() {
        return "TRK-" + UUID.randomUUID().toString().toUpperCase().replaceAll("-", "").substring(0, 10);
    }

    // this updates the status of the ordered items delivered when the order is delivered.
    public  Order updateOrderItemsDeliveredStatus(ShippingUpdateDTO shippingUpdateDTO){
        ShippingDetails shippingDetails = shippingRepo.findById(shippingUpdateDTO.getShippingId()).get();
        Order order = orderRepository.findById(shippingDetails.getOrderId()).get();
        List<OrderItem> orderItems = new ArrayList<>();
        if(shippingUpdateDTO.getNewValue().equalsIgnoreCase("DELIVERED")){
            orderItems = order.getOrderItems();
        }
        for(OrderItem item : orderItems){
            item.setStatus("DELIVERED");
        }
        order.setOrderItems(orderItems);
        Order order1 = orderRepository.save(order);
        return order1;
    }


    public String getCourierName(){
        List<String> courierNames = List.of(
                "Ekart Logistics",
                "Amazon Transportation Services",
                "Delhivery",
                "Shadowfax",
                "XpressBees"
        );
        String randomName = courierNames.get(ThreadLocalRandom.current().nextInt(courierNames.size()));
//        System.out.println(randomName);
        return randomName;
    }





}
