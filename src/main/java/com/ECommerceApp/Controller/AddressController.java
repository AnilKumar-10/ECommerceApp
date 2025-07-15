package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.Address;
import com.ECommerceApp.Service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/insertAddress")
    public Address insertUserAddress(@RequestBody Address address){
        return  addressService.createAddress(address);
    }

    @PostMapping("/insertAddresses")
    public String  insertUsersAddress(@RequestBody List<Address> address){
        return addressService.createAddresses(address);
    }

    @GetMapping("/getAddress/{id}")
    public List<Address> getUserAddress(@PathVariable String id){
        return addressService.getAddressesByUserId(id);
    }

    @GetMapping("/allAddress")
    public List<Address> getAddressess(){
        return addressService.getAllAddressess();
    }


}
