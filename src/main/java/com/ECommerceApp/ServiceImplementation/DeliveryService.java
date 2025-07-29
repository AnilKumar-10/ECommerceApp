package com.ECommerceApp.ServiceImplementation;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.ServiceInterface.*;
import com.ECommerceApp.DTO.Delivery.DeliveryItems;
import com.ECommerceApp.DTO.Delivery.DeliveryPersonRegistrationRequest;
import com.ECommerceApp.DTO.Delivery.DeliveryPersonResponse;
import com.ECommerceApp.Exceptions.Delivery.DeliveryNotFoundException;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Repository.DeliveryRepository;
import com.ECommerceApp.ServiceInterface.IDeliveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DeliveryService implements IDeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private IAddressService addressService;
    @Autowired
    private UserServiceInterface userService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IEmailService emailService;

    // Assign delivery person by address match
    public DeliveryPerson assignDeliveryPerson(String deliveryAddress) {
        log.info("Assigning the delivery Person to delivery the Order");
        List<DeliveryPerson> allPersons = deliveryRepository.findAll();
        String address = addressService.getAddressById(deliveryAddress).getCity();
        for (DeliveryPerson person : allPersons) {
            if(person.isActive()){
            for (String area : person.getAssignedAreas()) {
                if (area.toLowerCase().equalsIgnoreCase(address.toLowerCase())) {
                    log.info("The person to deliver is : "+person);
                    return person;
                }
            }}
        }
        log.info("There is no delivery agent available for that address: "+deliveryAddress);
        return null;
    }


    public DeliveryPerson register(DeliveryPersonRegistrationRequest deliveryPersonRegistrationDto){
        DeliveryPerson deliveryPerson  = new DeliveryPerson();
        BeanUtils.copyProperties(deliveryPersonRegistrationDto,deliveryPerson);
        deliveryPerson.setToReturnItems(new ArrayList<>());
        deliveryPerson.setToDeliveryItems(new ArrayList<>());
        deliveryPerson.setToDeliveryCount(0);
        deliveryPerson.setDeliveredCount(0);
        deliveryPerson.setActive(true);
        log.info("Delivery person registration is success: "+deliveryPerson);
        return save(deliveryPerson);
    }


    public String registerPersons(List<DeliveryPersonRegistrationRequest> deliveryPerson){
        int c=0;
        for(DeliveryPersonRegistrationRequest person: deliveryPerson){
            save(register(person));
            c++;
        }
        return "inserted: "+c;
    }


    public DeliveryPerson getDeliveryPerson(String id){
        return deliveryRepository.findById(id).orElseThrow(()->new DeliveryNotFoundException("No deliveryPerson found with id: "+id));
    }

    // assigning the packages to the delivery person
    public DeliveryPerson assignProductsToDelivery(String deliveryPersonId,Order order){
        System.out.println("inside the assignProductsToDelivery: "+deliveryPersonId);
        log.info("Assigning the products to be delivered to the delivery person");
        DeliveryPerson deliveryPerson = getDeliveryPerson(deliveryPersonId);
        DeliveryItems deliveryItems = new DeliveryItems();
        deliveryItems.setAddress(addressService.getAddressById(order.getAddressId()));
        deliveryItems.setShippingId(order.getShippingId());
        deliveryItems.setOrderId(order.getId());

        deliveryItems.setPaymentMode(order.getPaymentMethod().name());
        deliveryItems.setAmountToPay(order.getPaymentMethod() == Payment.PaymentMethod.COD ? order.getFinalAmount() : 0.0);
        deliveryItems.setUserName(userService.getUserById(order.getBuyerId()).getName());
        if(deliveryPerson.getToDeliveryItems().isEmpty()){
            deliveryPerson.setToDeliveryItems(new ArrayList<>());
        }
        deliveryPerson.getToDeliveryItems().add(deliveryItems);
        // sending the mail to delivery partner about the order is assigned to deliver
        log.info("sending the mail to the about the Order is assigned to the delivery to : "+deliveryPersonId);
        emailService.sendOrderAssignedToDeliveryPerson("iamanil3121@gmail.com",deliveryItems,deliveryPerson.getName(),deliveryPerson.getId());
        return save(deliveryPerson);
    }


    public DeliveryPerson updateDeliveryPerson(DeliveryPerson deliveryPerson){
        return save(deliveryPerson);
    }


    public void removeDeliveredOrderFromToDeliveryItems(String deliveryPersonId, String orderId) {
        log.info("Removing the delivered orders from the delivery agent: "+deliveryPersonId);
        Query query = new Query(Criteria.where("_id").is(deliveryPersonId));
        Update update = new Update().pull("toDeliveryItems", Query.query(Criteria.where("orderId").is(orderId)));
        mongoTemplate.updateFirst(query, update, DeliveryPerson.class);
    }


    public void updateDeliveryCount(String deliveryPersonId){
        log.info("Updating the delivery count after delivery done");
        DeliveryPerson deliveryPerson = getDeliveryPerson(deliveryPersonId);
        deliveryPerson.setToDeliveryCount(deliveryPerson.getToDeliveryCount()-1);
        deliveryPerson.setDeliveredCount(deliveryPerson.getDeliveredCount()+1);
        updateDeliveryPerson(deliveryPerson); // updating the delivery counts of the delivered person.

    }

    // here we are using the mongo template because jpa don't support the updates.
    public void removeReturnItemFromDeliveryPerson(String deliveryPersonId, String orderId) {
        log.info("Removing the to return items after the return delivery is success.");
        Query query = new Query(Criteria.where("_id").is(deliveryPersonId));
        Update update = new Update().pull("toReturnItems", Query.query(Criteria.where("orderId").is(orderId)));
        mongoTemplate.updateFirst(query, update, DeliveryPerson.class);
    }


    public void removeExchangeItemFromDeliveryPerson(String deliveryPersonId, String orderId) {
        log.info("Removing the to Exchange items after the exchange delivery is success.");
        Query query = new Query(Criteria.where("_id").is(deliveryPersonId));
        Update update = new Update().pull("toExchangeItems", Query.query(Criteria.where("orderId").is(orderId)));
        mongoTemplate.updateFirst(query, update, DeliveryPerson.class);
        DeliveryPerson deliveryPerson = getDeliveryPerson(deliveryPersonId);
        deliveryPerson.setDeliveredCount(deliveryPerson.getDeliveredCount()+1);
        save(deliveryPerson);
    }


    public String  deleteDeliveryMan(String id){
        if(!deliveryRepository.existsById(id)){
            throw new DeliveryNotFoundException("There is no delivery man present with that id: "+id);
        }
        deliveryRepository.deleteById(id);
        return "Deleted Successfully";
    }


    public DeliveryPersonResponse getDeliveryPersonByOrderId(String orderId){
        log.info("Getting the delivery person by order id: "+orderId);
        System.out.println("inside delivery service: "+orderId);
        DeliveryPerson deliveryPerson = deliveryRepository.findByOrderId(orderId).get();
        System.out.println(deliveryPerson);
        DeliveryPersonResponse deliveryPersonResponseDto = new DeliveryPersonResponse();
        BeanUtils.copyProperties(deliveryPerson,deliveryPersonResponseDto);
        log.info("the delivery man with order: "+orderId+ "  :  "+deliveryPersonResponseDto);
        return deliveryPersonResponseDto;
    }


    public void updateDeliveryCountAfterOrderCancellation(String deliveryPersonId){
        log.info("Updating the delivery Count after the order is cancelled : "+deliveryPersonId);
        DeliveryPerson deliveryPerson = getDeliveryPerson(deliveryPersonId);
        deliveryPerson.setToDeliveryCount(deliveryPerson.getToDeliveryCount()-1);
        updateDeliveryPerson(deliveryPerson); // updating the delivery counts of the delivered person.
    }


    public DeliveryPerson save(DeliveryPerson deliveryPerson){
        return  deliveryRepository.save(deliveryPerson);
    }

    public long totalCount() {
        return deliveryRepository.count();
    }

}
