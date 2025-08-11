package com.ECommerceApp.ServiceImplementation.User;

import com.ECommerceApp.DTO.Delivery.DeliveryPersonRegistrationRequest;
import com.ECommerceApp.DTO.Delivery.DeliveryPersonRegistrationResponse;
import com.ECommerceApp.DTO.User.PasswordUpdate;
import com.ECommerceApp.DTO.User.UserRegistrationRequest;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.ServiceImplementation.Delivery.DeliveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class AuthService {
    @Autowired
    private UserService userService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private OtpService otpService;


    public Users registerUser(UserRegistrationRequest registerRequest) {

        Users users = new Users();
        BeanUtils.copyProperties(registerRequest,users);
        userService.validateUserForRoles(users);
        users.setActive(true);
        users.setCreatedAt(new Date());
        users.setPasswordChangedAt(new Date());
        return userService.saveUser(users);
    }



    public DeliveryPersonRegistrationResponse register(DeliveryPersonRegistrationRequest deliveryPersonRegistrationDto){
        DeliveryPerson deliveryPerson  = new DeliveryPerson();
        BeanUtils.copyProperties(deliveryPersonRegistrationDto,deliveryPerson);
        deliveryPerson.setToReturnItems(new ArrayList<>());
        deliveryPerson.setToDeliveryItems(new ArrayList<>());
        deliveryPerson.setToExchangeItems(new ArrayList<>());
        deliveryPerson.setToDeliveryCount(0);
        deliveryPerson.setDeliveredCount(0);
        deliveryPerson.setActive(true);
        deliveryPerson.setPasswordChangedAt(new Date());
        log.info("Delivery person registration is success: {}", deliveryPerson);
        DeliveryPerson deliveryPerson1 =deliveryService.save(deliveryPerson);
        DeliveryPersonRegistrationResponse deliveryPersonRegistrationResponse = new DeliveryPersonRegistrationResponse();
        BeanUtils.copyProperties(deliveryPerson1,deliveryPersonRegistrationResponse);
        return deliveryPersonRegistrationResponse;
    }


    public String updateUserPassword(PasswordUpdate request) {
        log.info("updating the user password");
        Users user = userService.getUserByEmail(request.getEmail());
        user.setPassword(request.getNewPassword());
        user.setPasswordChangedAt(new Date());
        userService.saveUser(user);
        otpService.clearOtp(request.getEmail());
        return "Password updated successfully, Please Login again.";
    }


    public String updateDeliveryPassword(PasswordUpdate request) {
        log.info("updating the delivery person password");
        DeliveryPerson deliveryPerson = deliveryService.getDeliveryPersonByEmail(request.getEmail());
        deliveryPerson.setPassword(request.getNewPassword());
        deliveryPerson.setPasswordChangedAt(new Date());
        deliveryService.save(deliveryPerson);
        otpService.clearOtp(request.getEmail());
        return "Password updated successfully, Please Login again.";
    }
}
