package com.ECommerceApp.Controller.User;

import com.ECommerceApp.DTO.User.AddressRegistrationRequest;
import com.ECommerceApp.Model.User.Address;
import com.ECommerceApp.ServiceInterface.User.IAddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/address")
public class AddressController { // everyone

    @Autowired
    private IAddressService addressService;

    // All Roles — INSERT
    @PreAuthorize("hasPermission('ADDRESS', 'INSERT')")
    @PostMapping("/insertAddress")
    public ResponseEntity<?> insertUserAddress(@Valid @RequestBody AddressRegistrationRequest address){
        return ResponseEntity.ok(addressService.createAddress(address));
    }

    // All Roles — INSERT
    @PreAuthorize("hasPermission('ADDRESS', 'INSERT')")
    @PostMapping("/insertAddresses")
    public ResponseEntity<?> insertUsersAddress(@Valid @RequestBody List<@Valid AddressRegistrationRequest> address){
        return ResponseEntity.ok(addressService.createAddresses(address));
    }

    // SELF — READ
    @PreAuthorize("hasPermission('ADDRESS', 'READ')")
    @GetMapping("/getAddress/{id}")
    public List<Address> getUserAddress(@PathVariable String id){
        return addressService.getAddressesByUserId(id);
    }

    // ADMIN/SELLER — Read all addresses
    @PreAuthorize("hasPermission('ADDRESS', 'READ')")
    @GetMapping("/allAddress")
    public List<Address> getAddresses(){
        return addressService.getAllAddresses();
    }

    // SELF — UPDATE
    @PreAuthorize("hasPermission('ADDRESS', 'UPDATE')")
    @PutMapping("/updateAddress")
    public ResponseEntity<?> updateAddress(@Valid @RequestBody Address address){
        return ResponseEntity.ok(addressService.updateAddress("", address));
    }

    // SELF — DELETE
    @PreAuthorize("hasPermission('ADDRESS', 'DELETE')")
    @DeleteMapping("/deleteAddress/{addressId}")
    public String deleteAddress(@PathVariable String addressId){
        return addressService.deleteAddress(addressId);
    }
}
