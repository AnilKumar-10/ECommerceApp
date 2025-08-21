package com.ECommerceApp.Mappers;

import com.ECommerceApp.DTO.User.AddressRegistrationRequest;
import com.ECommerceApp.Model.User.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toAddress(AddressRegistrationRequest request);

}
