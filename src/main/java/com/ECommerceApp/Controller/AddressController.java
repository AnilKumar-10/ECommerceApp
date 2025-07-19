package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.AddressRegistrationDto;
import com.ECommerceApp.Model.Address;
import com.ECommerceApp.Service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class AddressController { // everyone

    @Autowired
    private AddressService addressService;

    @PostMapping("/insertAddress")//all users
    public ResponseEntity<?> insertUserAddress(@Valid  @RequestBody AddressRegistrationDto address){
        return  ResponseEntity.ok(addressService.createAddress(address));
    }

    @PostMapping("/insertAddresses")//all users
    public ResponseEntity<?>  insertUsersAddress(@Valid @RequestBody List<@Valid AddressRegistrationDto> address){
        return ResponseEntity.ok(addressService.createAddresses(address));
    }

    @GetMapping("/getAddress/{id}")  // all users
    public List<Address> getUserAddress(@PathVariable String id){
        return addressService.getAddressesByUserId(id);
    }

    @GetMapping("/allAddress")
    public List<Address> getAddresses(){
        return addressService.getAllAddresses();
    }

    @PutMapping("/updateAddress")
    public ResponseEntity<?> updateAddress(@Valid @RequestBody Address address){
        return ResponseEntity.ok(addressService.updateAddress("",address));
    }

    @DeleteMapping("/deleteAddress/{addressId}")
    public String deleteAddress(@PathVariable String addressId){
        return addressService.deleteAddress(addressId);
    }
}
