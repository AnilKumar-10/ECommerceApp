package com.ECommerceApp.Controller.User;

import com.ECommerceApp.DTO.Delivery.DeliveryPersonRegistrationRequest;
import com.ECommerceApp.DTO.User.*;
import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.ServiceImplementation.Delivery.DeliveryService;
import com.ECommerceApp.ServiceImplementation.User.AuthService;
import com.ECommerceApp.ServiceImplementation.User.OtpService;
import com.ECommerceApp.ServiceImplementation.User.UserService;
import com.ECommerceApp.ServiceImplementation.UserDetailService.CustomUserDetails;
import com.ECommerceApp.ServiceImplementation.UserDetailService.UserDetailsServiceImpl;
import com.ECommerceApp.Util.JwtUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private JwtUtils jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private AuthService authService;
    @Autowired
    private OtpService otpService;


    //  1. Register USER / SELLER / ADMIN
    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        if (userService.existsByMail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        }
        UserRegistrationResponse userResponse = new UserRegistrationResponse();
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        Users user = authService.registerUser(request);
        BeanUtils.copyProperties(user,userResponse);
        return ResponseEntity.ok(userResponse);
    }

    //  2. Register DELIVERY_PERSON
    @PostMapping("/delivery/register")
    public ResponseEntity<?> registerDeliveryPerson(@Valid @RequestBody DeliveryPersonRegistrationRequest request) {
        if (deliveryService.existsByMail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        return ResponseEntity.ok(authService.register(request));
    }

    // 3. Login (for USERS only, not delivery personnel)
    @PostMapping("/user/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request) {
        Users user = userService.getUserByEmail(request.getEmail());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);
        LoginResponse loginResponse = new LoginResponse();
        BeanUtils.copyProperties(user,loginResponse);
        loginResponse.setToken(token);
        return ResponseEntity.ok(loginResponse);

    }


    @PostMapping("/delivery/login")
    public ResponseEntity<?> deliveryLogin(@Valid @RequestBody UserLoginRequest request) {
        log.info("inside delver login");
        DeliveryPerson deliveryPerson = deliveryService.getDeliveryPersonByEmail(request.getEmail());
        if (!passwordEncoder.matches(request.getPassword(), deliveryPerson.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(deliveryPerson.getEmail());
        String token = jwtService.generateToken(userDetails);
        LoginResponse loginResponse = new LoginResponse();
        BeanUtils.copyProperties(deliveryPerson,loginResponse);
        loginResponse.setToken(token);
        return ResponseEntity.ok(loginResponse);

    }

    // for checking the authentication object.
    @GetMapping("/auth-check")
    public ResponseEntity<String> checkAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        System.out.println("User Authorities: " + auth.getAuthorities());
        System.out.println("User mail: " + auth.getName());
        System.out.println("User id: " + customUserDetails.getUserId());
        return ResponseEntity.ok("Checked roles, see console log.");
    }



}
