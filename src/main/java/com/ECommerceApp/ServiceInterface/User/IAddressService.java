package com.ECommerceApp.ServiceInterface.User;

import com.ECommerceApp.DTO.User.AddressRegistrationRequest;
import com.ECommerceApp.Model.User.Address;

import java.util.List;

public interface IAddressService {

    Address createAddress(AddressRegistrationRequest address);

    String createAddresses(List<AddressRegistrationRequest> addresses);

    List<Address> getAddressesByUserId(String userId);

    Address getAddressById(String id);

    Address updateAddress(String id, Address address);

    String deleteAddress(String id);

    List<Address> getAllAddresses();

    Address getAddressBy
}
