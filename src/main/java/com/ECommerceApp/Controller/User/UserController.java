package com.ECommerceApp.Controller.User;

import com.ECommerceApp.DTO.User.SellerResponse;
import com.ECommerceApp.DTO.User.UserRegistrationRequest;
import com.ECommerceApp.DTO.User.UserRegistrationResponse;
import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.ServiceInterface.IDeliveryService;
import com.ECommerceApp.ServiceInterface.UserServiceInterface;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserServiceInterface userService;
    @Autowired
    private IDeliveryService deliveryService;

    @PostMapping("/insertUser")
    public UserRegistrationResponse insertUser(@Valid @RequestBody UserRegistrationRequest users){
        UserRegistrationResponse userResponse = new UserRegistrationResponse();
        Users user =userService.registerUser(users);
        BeanUtils.copyProperties(userResponse,user);
        return  userResponse;
    }


    @PostMapping("/insertUsers")
    public String insertUsers(@Valid @RequestBody List<UserRegistrationRequest> users){
        return  userService.registerUsers(users);
    }


    @GetMapping("/getAllUsers")
    public List<UserRegistrationResponse> getAllUsers(){
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
    public List<UserRegistrationResponse> getUserByRole(@PathVariable String role){
        return userService.getUsersByRole(role);
    }


    @PutMapping("/addRole")
    public UserRegistrationResponse addRoleToUser(@RequestBody Map<String, String> map){
        String userId = map.get("userId");
        Users.Role newRole = Users.Role.valueOf(map.get("newRole"));
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
