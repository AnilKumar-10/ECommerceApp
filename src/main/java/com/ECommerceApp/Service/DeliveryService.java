package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.DeliveryUpdateDTO;
import com.ECommerceApp.DTO.ShippingUpdateDTO;
import com.ECommerceApp.Model.DeliveryPerson;
import com.ECommerceApp.Model.Order;
import com.ECommerceApp.Repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private AddressService addressService;

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

//    public void updateOrder(DeliveryUpdateDTO deliveryUpdateDTO){
//
//    }



}
