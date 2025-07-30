package com.ECommerceApp.ServiceInterface;

import com.ECommerceApp.DTO.User.SellerResponse;
import com.ECommerceApp.DTO.User.UserRegistrationRequest;
import com.ECommerceApp.DTO.User.UserResponse;
import com.ECommerceApp.Model.User.Users;

import java.util.List;

public interface UserServiceInterface {

    Users registerUser(UserRegistrationRequest user);

    String registerUsers(List<UserRegistrationRequest> users);

    Users updateUser(String userId, UserRegistrationRequest updatedData);

    Users deactivateUser(String userId);

    Users getUserById(String id);

    Users getUserByEmail(String email);

    List<UserResponse> getUsersByRole(String role);

    UserResponse addRoleToUser(String userId, Users.Role newRole);

    List<UserResponse> getAllUsers();

    List<SellerResponse> getAllSellers();

    long getTotalUserCount();

    long getTotalSellersCount();

    Users saveUser(Users users);
}

