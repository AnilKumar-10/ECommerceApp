package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.DeliveryItems;
import com.ECommerceApp.DTO.DeliveryUpdateDTO;
import com.ECommerceApp.DTO.ShippingUpdateDTO;
import com.ECommerceApp.Model.DeliveryPerson;
import com.ECommerceApp.Model.Order;
import com.ECommerceApp.Repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

}
