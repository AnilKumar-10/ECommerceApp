package com.ECommerceApp.ServiceImplementation.User;

import com.ECommerceApp.DTO.User.AddressRegistrationRequest;
import com.ECommerceApp.Exceptions.User.AddressNotFoundException;
import com.ECommerceApp.Model.User.Address;
import com.ECommerceApp.Repository.AddressRepository;
import com.ECommerceApp.ServiceInterface.User.IAddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService implements IAddressService {
    @Autowired
    private AddressRepository addressRepository;

    // inserting the new address
    public Address createAddress(AddressRegistrationRequest addressRequest) {
        Address address = new Address();
        BeanUtils.copyProperties(addressRequest,address);
        return saveAddress(address);
    }

    public String  createAddresses(List<AddressRegistrationRequest> addresses) {
        int c=0;
        for(AddressRegistrationRequest address:addresses){
            createAddress(address);
            c++;
        }
         return "done: "+c;
    }

    // getting the address based on the userid
    public List<Address> getAddressesByUserId(String userId) {
        return addressRepository.findByUserId(userId);
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
        return saveAddress(existing);
    }

    // delete the address based on id.
    public String  deleteAddress(String id) {
        if (!addressRepository.existsById(id)) {
            throw new AddressNotFoundException("Address not found with ID: " + id);
        }
        addressRepository.deleteById(id);
        return "Deleted successfully";
    }

    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }


    public Address saveAddress(Address address){
        return addressRepository.save(address);
    }

}
