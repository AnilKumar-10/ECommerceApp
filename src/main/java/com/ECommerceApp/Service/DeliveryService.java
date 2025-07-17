package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.*;
import com.ECommerceApp.Exceptions.DeliveryNotFoundException;
import com.ECommerceApp.Model.DeliveryPerson;
import com.ECommerceApp.Model.Order;
import com.ECommerceApp.Repository.DeliveryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public String  registerPersons(List<DeliveryPerson> deliveryPerson){
        int c=0;
        for(DeliveryPerson person: deliveryPerson){
            deliveryRepository.save(person);
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


    public DeliveryPerson updateDeliveryPerson(DeliveryPerson deliveryPerson){
        return deliveryRepository.save(deliveryPerson);
    }


    public void removeDeliveredOrderFromToDeliveryItems(String deliveryPersonId, String orderId) {
        Query query = new Query(Criteria.where("_id").is(deliveryPersonId));
        Update update = new Update().pull("toDeliveryItems", Query.query(Criteria.where("OrderId").is(orderId)));
        System.out.println("inside remove delivery orders: "+query+"  =  "+update);
        mongoTemplate.updateFirst(query, update, DeliveryPerson.class);
    }


    public void updateDeliveryCount(String deliveryPersonId){
        DeliveryPerson deliveryPerson = getDeliveryPerson(deliveryPersonId);
        deliveryPerson.setToDeliveryCount(deliveryPerson.getToDeliveryCount()-1);
        deliveryPerson.setDeliveredCount(deliveryPerson.getDeliveredCount()+1);
        updateDeliveryPerson(deliveryPerson); // updating the delivery counts of the delivered person.

    }


    public void removeReturnItemFromDeliveryPerson(String deliveryPersonId, String orderId) {
        Query query = new Query(Criteria.where("_id").is(deliveryPersonId));
        Update update = new Update().pull("toReturnItems", Query.query(Criteria.where("orderId").is(orderId)));
        mongoTemplate.updateFirst(query, update, DeliveryPerson.class);
    }


    public String  deleteDeliveryMan(String id){
        if(!deliveryRepository.existsById(id)){
            throw new DeliveryNotFoundException("There is no delivery man present with that id: "+id);
        }
        deliveryRepository.deleteById(id);
        return "Deleted Successfully";
    }

//    public Optional<DeliveryItems> getDeliveryItemByOrderId(String deliveryPersonId, String orderId) {
//        Optional<DeliveryPerson> deliveryPersonOptional =
//                deliveryRepository.findSingleDeliveryItemByOrderId(deliveryPersonId, orderId);
//
//        if (deliveryPersonOptional.isPresent()) {
//            List<DeliveryItems> items = deliveryPersonOptional.get().getToDeliveryItems();
//            if (items != null && !items.isEmpty()) {
//                return Optional.of(items.get(0)); // Only one item is returned by the query
//            }
//        }
//        return Optional.empty();
//    }


    public DeliveryPersonResponseDto getDeliveryPersonByOrderId(String orderId){
        DeliveryPerson deliveryPerson = deliveryRepository.findByOrderId(orderId).get();
        DeliveryPersonResponseDto deliveryPersonResponseDto = new DeliveryPersonResponseDto();
        BeanUtils.copyProperties(deliveryPerson,deliveryPersonResponseDto);
        return deliveryPersonResponseDto;
    }

}
