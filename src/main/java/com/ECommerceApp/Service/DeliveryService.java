package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.*;
import com.ECommerceApp.Model.DeliveryPerson;
import com.ECommerceApp.Model.Order;
import com.ECommerceApp.Repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;
    @Autowired
    private MongoTemplate mongoTemplate;

//    @Autowired
//    private OrderService orderService;

    // Assign delivery person by address match
    public DeliveryPerson assignDeliveryPerson(String deliveryAddress) {
        List<DeliveryPerson> allPersons = deliveryRepository.findAll();
        String address = addressService.getAddressById(deliveryAddress).getCity();
        System.out.println("city: "+address);
        for (DeliveryPerson person : allPersons) {
            for (String area : person.getAssignedAreas()) {
                System.out.println("city: "+address+"  ===>   "+area);
                if (area.toLowerCase().equalsIgnoreCase(address.toLowerCase())) {
                    return person;
                }
            }
        }
        return null;
    }

    public DeliveryPerson register(DeliveryPerson deliveryPerson){
        return deliveryRepository.save(deliveryPerson);
    }


    public DeliveryPerson getDeliveryPerson(String id){
        return deliveryRepository.findById(id).get();
    }

    // assigning the packages to the delivery person
    public DeliveryPerson assignProductsToDelivery(String deliveryPersonId,Order order){
        System.out.println("inside the assignProductsToDelivery: "+deliveryPersonId);
        DeliveryPerson deliveryPerson = getDeliveryPerson(deliveryPersonId);
        DeliveryItems deliveryItems = new DeliveryItems();
        deliveryItems.setAddress(addressService.getAddressById(order.getAddressId()));
        deliveryItems.setShippingId(order.getShippingId());
        deliveryItems.setOrderId(order.getId());
        deliveryItems.setPaymentMode(order.getPaymentMethod());
        deliveryItems.setAmountToPay(order.getPaymentMethod().equalsIgnoreCase("COD")? order.getFinalAmount() : 0.0);
        deliveryItems.setUserName(userService.getUserById(order.getBuyerId()).getName());
        if(deliveryPerson.getToDeliveryItems().isEmpty()){
            deliveryPerson.setToDeliveryItems(new ArrayList<>());
        }
        deliveryPerson.getToDeliveryItems().add(deliveryItems);
        return deliveryRepository.save(deliveryPerson);
    }


//    public DeliveryPerson getDeliveryPersonByShippingId(String shippingId){
//
//        return deliveryRepository.findById();
//    }


    public DeliveryPerson updateDeliveryPerson(DeliveryPerson deliveryPerson){
        return deliveryRepository.save(deliveryPerson);
    }


    public void removeDeliveredOrderFromToDeliveryItems(String deliveryPersonId, String orderId) {
        Query query = new Query(Criteria.where("_id").is(deliveryPersonId));
        Update update = new Update().pull("toDeliveryItems", Query.query(Criteria.where("orderId").is(orderId)));
        mongoTemplate.updateFirst(query, update, DeliveryPerson.class);
    }


    public void updateDeliveryCount(String deliveryPersonId){
        DeliveryPerson deliveryPerson = getDeliveryPerson(deliveryPersonId);
        deliveryPerson.setToDeliveryCount(deliveryPerson.getToDeliveryCount()-1);
        deliveryPerson.setDeliveredCount(deliveryPerson.getDeliveredCount()+1);
        updateDeliveryPerson(deliveryPerson); // updating the delivery counts of the delivered person.

    }

}
