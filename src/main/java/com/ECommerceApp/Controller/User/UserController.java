package com.ECommerceApp.Controller.User;

import com.ECommerceApp.DTO.User.SellerResponse;
import com.ECommerceApp.DTO.User.UserRegistrationRequest;
import com.ECommerceApp.DTO.User.UserResponse;
import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.Service.DeliveryService;
import com.ECommerceApp.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private DeliveryService deliveryService;

    @PostMapping("/insertUser")
    public UserResponse insertUser(@Valid @RequestBody UserRegistrationRequest users){
        UserResponse userResponse = new UserResponse();
        Users user =userService.registerUser(users);
        BeanUtils.copyProperties(userResponse,user);
        return  userResponse;
    }

    @PostMapping("/insertUsers")
    public String insertUsers(@Valid @RequestBody List<UserRegistrationRequest> users){
        return  userService.registerUsers(users);
    }

    @GetMapping("/getAllUsers")
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/getUserById/{userId}")
    public Users getUserById(@PathVariable String userId){
        return userService.getUserById(userId);
    }

    @PutMapping("/updateUser")
    public Users updateUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest){
        return  userService.updateUser(userRegistrationRequest.getId(), userRegistrationRequest);
    }

    @PostMapping("/deactivateUser/{userId}")
    public Users deActivateUser(@PathVariable String userId){
        return userService.deactivateUser(userId);
    }

    @GetMapping("/getUserByMail")
    public Users getUserByEmail(@Valid @RequestBody Map<String,String > map){
        return userService.getUserByEmail(map.get("email"));
    }

    @GetMapping("/getUserByRole/{role}")
    public List<UserResponse> getUserByRole(@PathVariable String role){
        return userService.getUsersByRole(role);
    }

    @PutMapping("/addRole")
    public UserResponse addRoleToUser(@RequestBody Map<String,String > map){
        String userId = map.get("userId");
        String newRole = map.get("newRole");
        return  userService.addRoleToUser(userId,newRole);
    }

    @GetMapping("/getSellers")
    public List<SellerResponse> getAllSellers(){
        return userService.getAllSellers();
    }

    @GetMapping("/getDeliveryCount")
    public long getDeliveryAgentCount(){
        return  deliveryService.totalCount();
    }
}
