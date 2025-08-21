package com.ECommerceApp.Mappers;

import com.ECommerceApp.DTO.Delivery.DeliveryPersonRegistrationRequest;
import com.ECommerceApp.DTO.Delivery.DeliveryPersonRegistrationResponse;
import com.ECommerceApp.DTO.Delivery.DeliveryPersonResponse;
import com.ECommerceApp.DTO.User.LoginResponse;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryPMapper {
    DeliveryPersonRegistrationResponse toDeliveryPersonRegistrationResponse(DeliveryPerson deliveryPerson);

    DeliveryPersonResponse toDeliveryPersonResponse(DeliveryPerson deliveryPerson);

    DeliveryPerson toDeliveryPerson(DeliveryPersonRegistrationRequest request);

    LoginResponse toLoginResponse(DeliveryPerson deliveryPerson);
}
