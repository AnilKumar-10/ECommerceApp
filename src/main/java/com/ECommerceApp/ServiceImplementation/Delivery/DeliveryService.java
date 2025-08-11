package com.ECommerceApp.ServiceImplementation.Delivery;
import com.ECommerceApp.Exceptions.Delivery.DeliveryPersonNotFound;
import com.ECommerceApp.Model.Payment.Payment;
import com.ECommerceApp.DTO.Delivery.DeliveryItems;
import com.ECommerceApp.DTO.Delivery.DeliveryPersonResponse;
import com.ECommerceApp.Exceptions.Delivery.DeliveryNotFoundException;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.Order.Order;
import com.ECommerceApp.Repository.DeliveryRepository;
import com.ECommerceApp.ServiceInterface.Delivery.IDeliveryService;
import com.ECommerceApp.ServiceInterface.User.IAddressService;
import com.ECommerceApp.ServiceInterface.User.IEmailService;
import com.ECommerceApp.ServiceInterface.User.UserServiceInterface;
import com.ECommerceApp.Util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
                if (area.equalsIgnoreCase(address)) {
                    log.info("The person to deliver is : {}", person);
                    return person;
                }
            }}
        }
        log.info("There is no delivery agent available for your address: {}", deliveryAddress);
        return null;
    }


    public DeliveryPerson getDeliveryPerson(String id){
        return deliveryRepository.findById(id).orElseThrow(()->new DeliveryNotFoundException("No deliveryPerson found with id: "+id));
    }

    // assigning the packages to the delivery person
    public DeliveryPerson assignProductsToDelivery(String deliveryPersonId,Order order){
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
        log.info("sending the mail to the about the Order is assigned to the delivery to : {}", deliveryPersonId);
        emailService.sendOrderAssignedToDeliveryPerson("iamanil3121@gmail.com",deliveryItems,deliveryPerson.getName(),deliveryPerson.getId());
        return save(deliveryPerson);
    }


    public DeliveryPerson updateDeliveryPerson(DeliveryPerson deliveryPerson){
        return save(deliveryPerson);
    }


    public void removeDeliveredOrderFromToDeliveryItems(String deliveryPersonId, String orderId) {
        log.info("Removing the delivered orders from the delivery agent: {}", deliveryPersonId);
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
        deliveryPerson.setToDeliveryCount(deliveryPerson.getToDeliveryCount()-1);
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
        log.info("Getting the delivery person by order id: {}", orderId);
        DeliveryPerson deliveryPerson = deliveryRepository.findByOrderId(orderId).get();
        System.out.println(deliveryPerson);
        DeliveryPersonResponse deliveryPersonResponseDto = new DeliveryPersonResponse();
        BeanUtils.copyProperties(deliveryPerson,deliveryPersonResponseDto);
        log.info("the delivery man with order: {}  :  {}", orderId, deliveryPersonResponseDto);
        return deliveryPersonResponseDto;
    }


    public void updateDeliveryCountAfterOrderCancellation(String deliveryPersonId){
        log.info("Updating the delivery Count after the order is cancelled : {}", deliveryPersonId);
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

    @Override
    public DeliveryPerson getDeliveryPeronData() {
        String email = new SecurityUtils().getCurrentUserMail();
        return deliveryRepository.findByEmail(email).get();
    }


    public boolean existsByMail(String email){
        return deliveryRepository.existsByEmail(email);
    }

    public DeliveryPerson getDeliveryPersonByEmail(String email){
        return deliveryRepository.findByEmail(email).orElseThrow(()-> new DeliveryPersonNotFound("There is no delivery person with mail: "+email));
    }


    public Optional<DeliveryPerson> loadDeliveryByMail(String email){
        return deliveryRepository.findByEmail(email);
    }

    public DeliveryPerson updateDelivery(DeliveryPerson deliveryPerson){
        DeliveryPerson exist = deliveryRepository.findByEmail(deliveryPerson.getEmail()).get();
        exist.setPasswordChangedAt(new Date());
        exist.setPassword(deliveryPerson.getPassword());
        exist.setRoles(deliveryPerson.getRoles());
        return updateDeliveryPerson(exist);
    }


    public String getNameById(String id) {
        return getDeliveryPerson(id).getName();
    }
}
