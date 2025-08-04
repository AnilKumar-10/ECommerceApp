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
import org.springframework.security.access.prepost.PreAuthorize;
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


    //  SELF (READ) - only logged-in user
    @PreAuthorize("hasPermission('USER', 'READ')")
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp() {
        String email = new SecurityUtils().getCurrentUserMail();
        otpService.sendOtpToEmail(email);
        return ResponseEntity.ok("OTP sent to " + email);
    }

    //  SELF (UPDATE)
    @PreAuthorize("hasPermission('USER', 'UPDATE')")
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

    //  ADMIN (READ)
    @PreAuthorize("hasPermission('USER', 'READ')")
    @GetMapping("/getAllUsers")
    public List<UserRegistrationResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    //  ADMIN (READ)
    @PreAuthorize("hasPermission('USER', 'READ')")
    @GetMapping("/getUserById/{userId}")
    public Users getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    //  SELF (UPDATE)
    @PreAuthorize("hasPermission('USER', 'UPDATE')")
    @PutMapping("/updateUser")
    public Users updateUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        userRegistrationRequest.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
        return userService.updateUser(userRegistrationRequest.getId(), userRegistrationRequest);
    }

    //  ADMIN (DELETE)
    @PreAuthorize("hasPermission('USER', 'DELETE')")
    @PostMapping("/deactivateUser/{userId}")
    public Users deActivateUser(@PathVariable String userId) {
        return userService.deactivateUser(userId);
    }

    //  ADMIN/SELF (READ)
    @PreAuthorize("hasPermission('USER', 'READ')")
    @GetMapping("/getUserByMail")
    public Users getUserByEmail(@Valid @RequestBody Map<String, String> map) {
        return userService.getUserByEmail(map.get("email"));
    }

    //  ADMIN (READ)
    @PreAuthorize("hasPermission('USER', 'READ')")
    @GetMapping("/getUserByRole/{role}")
    public List<UserRegistrationResponse> getUserByRole(@PathVariable String role) {
        return userService.getUsersByRole(role);
    }

    //  ADMIN (UPDATE)
    @PreAuthorize("hasPermission('USER', 'UPDATE')")
    @PutMapping("/addRole")
    public UserRegistrationResponse addRoleToUser(@RequestBody Map<String, String> map) {
        String userId = map.get("userId");
        Users.Role newRole = Users.Role.valueOf(map.get("newRole"));
        return userService.addRoleToUser(userId, newRole);
    }

    //  ADMIN (READ)
    @PreAuthorize("hasPermission('USER', 'READ')")
    @GetMapping("/getSellers")
    public List<SellerResponse> getAllSellers() {
        return userService.getAllSellers();
    }

    //  ADMIN (READ)
    @PreAuthorize("hasPermission('DELIVERY', 'READ')")
    @GetMapping("/getDeliveryCount")
    public long getDeliveryAgentCount() {
        return deliveryService.totalCount();
    }

}