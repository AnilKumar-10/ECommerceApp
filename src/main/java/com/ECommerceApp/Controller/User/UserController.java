package com.ECommerceApp.Controller.User;

import com.ECommerceApp.DTO.User.PasswordUpdate;
import com.ECommerceApp.DTO.User.SellerResponse;
import com.ECommerceApp.DTO.User.UserRegistrationRequest;
import com.ECommerceApp.DTO.User.UserRegistrationResponse;
import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.ServiceImplementation.AuthService;
import com.ECommerceApp.ServiceImplementation.OtpService;
import com.ECommerceApp.ServiceInterface.IDeliveryService;
import com.ECommerceApp.ServiceInterface.UserServiceInterface;
import com.ECommerceApp.Util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired
    private AuthService authService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp() {
        String email = new SecurityUtils().getCurrentUserMail();
        otpService.sendOtpToEmail(email);
        return ResponseEntity.ok("OTP sent to " + email);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid PasswordUpdate request) {
        boolean isValidOtp = otpService.validateOtp(request.getEmail(), request.getOtp());
        if (!isValidOtp) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        request.setNewPassword(passwordEncoder.encode(request.getNewPassword()));
        String response = authService.updateUserPassword(request);
        return ResponseEntity.ok(response);
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
        userRegistrationRequest.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
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
