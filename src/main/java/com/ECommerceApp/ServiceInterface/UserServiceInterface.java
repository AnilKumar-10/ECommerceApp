package com.ECommerceApp.ServiceInterface;

import com.ECommerceApp.DTO.User.SellerResponse;
import com.ECommerceApp.DTO.User.UserRegistrationRequest;
import com.ECommerceApp.DTO.User.UserRegistrationResponse;
import com.ECommerceApp.Model.User.Users;

import java.util.List;

public interface UserServiceInterface {

    Users registerUser(UserRegistrationRequest user);

    String registerUsers(List<UserRegistrationRequest> users);

    Users updateUser(String userId, UserRegistrationRequest updatedData);

    Users deactivateUser(String userId);

    Users getUserById(String id);

    Users getUserByEmail(String email);

    List<UserRegistrationResponse> getUsersByRole(String role);

    UserRegistrationResponse addRoleToUser(String userId, Users.Role newRole);

    List<UserRegistrationResponse> getAllUsers();

    List<SellerResponse> getAllSellers();

    long getTotalUserCount();

    long getTotalSellersCount();

    Users saveUser(Users users);

    public boolean existsByMail(String email);
}

