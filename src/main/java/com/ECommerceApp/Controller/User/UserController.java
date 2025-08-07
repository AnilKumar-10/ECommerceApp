package com.ECommerceApp.Controller.User;

import com.ECommerceApp.DTO.User.PasswordUpdate;
import com.ECommerceApp.DTO.User.SellerResponse;
import com.ECommerceApp.DTO.User.UserRegistrationRequest;
import com.ECommerceApp.DTO.User.UserRegistrationResponse;
import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.ServiceImplementation.User.AuthService;
import com.ECommerceApp.ServiceImplementation.User.OtpService;
import com.ECommerceApp.ServiceInterface.Delivery.IDeliveryService;
import com.ECommerceApp.ServiceInterface.User.UserServiceInterface;
import com.ECommerceApp.Util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
            throw new RuntimeException("Invalid OTP");
        }
        request.setNewPassword(passwordEncoder.encode(request.getNewPassword()));
        String response = authService.updateUserPassword(request);
        return ResponseEntity.ok(response);
    }

    //  ADMIN (READ)
    @PreAuthorize("hasPermission('USER', 'READ')")
    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    //  ADMIN (READ)
    @PreAuthorize("hasPermission('#userId','com.ECommerceApp.Model.User','READ')")
    @GetMapping("/getUserById/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    //  SELF (UPDATE)
    @PreAuthorize("hasPermission('USER', 'UPDATE')")
    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        String userId = new SecurityUtils().getCurrentUserId();
        return ResponseEntity.ok(userService.updateUser(userId, userRegistrationRequest));
    }

    //  ADMIN (DELETE)
    @PreAuthorize("hasPermission('USER', 'DELETE')")
    @PostMapping("/deactivateUser/{userId}")
    public ResponseEntity<?> deActivateUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.deactivateUser(userId));
    }

    //  ADMIN/SELF (READ)
    @PreAuthorize("hasPermission('USER', 'READ')")
    @GetMapping("/getUserByMail")
    public ResponseEntity<?> getUserByEmail(@Valid @RequestBody Map<String, String> map) {
        return ResponseEntity.ok(userService.getUserByEmail(map.get("email")));
    }

    //  ADMIN (READ)
    @PreAuthorize("hasPermission('USER', 'READ')")
    @GetMapping("/getUserByRole/{role}")
    public ResponseEntity<?> getUserByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    //  ADMIN (UPDATE)
    @PreAuthorize("hasPermission('USER', 'UPDATE')")
    @PutMapping("/addRole")
    public ResponseEntity<?> addRoleToUser(@RequestBody Map<String, String> map) {
        String userId = map.get("userId");
        Users.Role newRole = Users.Role.valueOf(map.get("newRole"));
        return ResponseEntity.ok(userService.addRoleToUser(userId, newRole));
    }

    //  ADMIN (READ)
    @PreAuthorize("hasPermission('USER', 'READ')")
    @GetMapping("/getSellers")
    public ResponseEntity<?> getAllSellers() {
        return ResponseEntity.ok(userService.getAllSellers());
    }

    //  ADMIN (READ)
    @PreAuthorize("hasPermission('DELIVERY', 'READ')")
    @GetMapping("/getDeliveryCount")
    public long getDeliveryAgentCount() {
        return deliveryService.totalCount();
    }

}