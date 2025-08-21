package com.ECommerceApp.Mappers;


import com.ECommerceApp.DTO.User.LoginResponse;
import com.ECommerceApp.DTO.User.SellerResponse;
import com.ECommerceApp.DTO.User.UserRegistrationRequest;
import com.ECommerceApp.DTO.User.UserRegistrationResponse;
import com.ECommerceApp.Model.User.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserRegistrationResponse toRegistrationResponse(Users users);

    SellerResponse toSellerResponse( Users users);

    LoginResponse toLoginResponse(Users users) ;

    Users toUser(UserRegistrationRequest request);
}

