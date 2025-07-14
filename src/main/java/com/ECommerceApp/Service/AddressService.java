package com.ECommerceApp.Service;

import com.ECommerceApp.Exceptions.AddressNotFoundException;
import com.ECommerceApp.Model.Address;
import com.ECommerceApp.Repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserService userService;

    // inserting the new address
    public Address createAddress(Address address) {
        return addressRepository.save(address);
    }

    public String  createAddresses(List<Address> addresses) {
        int c=0;
        for(Address address:addresses){
            addressRepository.save(address);
            c++;
        }
         return "done: "+c;
    }
    // getting the address based on the userid
    public List<Address> getAddressesByUserId(String userId) {

        return addressRepository.findByUserId(userId);
//                .orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " ));
    }

    //  getting by id
    public Address getAddressById(String id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " + id));
    }

    // updating the address
    public Address updateAddress(String id, Address address) {
        Address existing = getAddressById(id);
        existing.setStreet(address.getStreet());
        existing.setCity(address.getCity());
        existing.setState(address.getState());
        existing.setCountry(address.getCountry());
        existing.setPostalCode(address.getPostalCode());
        existing.setType(address.getType());
        return addressRepository.save(existing);
    }

    // delete the address based on id.
    public void deleteAddress(String id) {
        if (!addressRepository.existsById(id)) {
            throw new AddressNotFoundException("Address not found with ID: " + id);
        }
        addressRepository.deleteById(id);
    }

    public List<Address> getAllAddressess() {
        return addressRepository.findAll();
    }
}
