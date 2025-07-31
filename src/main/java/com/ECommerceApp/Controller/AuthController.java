package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.Delivery.DeliveryPersonRegistrationRequest;
import com.ECommerceApp.DTO.User.LoginResponse;
import com.ECommerceApp.DTO.User.UserLoginRequest;
import com.ECommerceApp.DTO.User.UserRegistrationRequest;
import com.ECommerceApp.DTO.User.UserRegistrationResponse;
import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.ServiceImplementation.DeliveryService;
import com.ECommerceApp.ServiceImplementation.UserDetailsServiceImpl;
import com.ECommerceApp.ServiceImplementation.UserService;
import com.ECommerceApp.Util.JwtService;
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
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    // ✅ 1. Register USER / SELLER / ADMIN
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest request) {

        if (userService.existsByMail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        }
        UserRegistrationResponse userResponse = new UserRegistrationResponse();
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        Users user =userService.registerUser(request);
        BeanUtils.copyProperties(user,userResponse);
        return ResponseEntity.ok(userResponse);
    }

    // ✅ 2. Register DELIVERY_PERSON
    @PostMapping("/register/delivery")
    public ResponseEntity<?> registerDeliveryPerson(@Valid @RequestBody DeliveryPersonRegistrationRequest request) {
        if (deliveryService.existsByMail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone already in use");
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        return ResponseEntity.ok(deliveryService.register(request));
    }

    // ✅ 3. Login (for USERS only, not delivery personnel)
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request) {
        Users user = userService.getUserByEmail(request.getMail());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);
        log.info("toke is: "+token);
        log.info("user is: "+user);
        LoginResponse loginResponse = new LoginResponse();
        BeanUtils.copyProperties(user,loginResponse);
        loginResponse.setToken(token);
        return ResponseEntity.ok(loginResponse);

    }
    @GetMapping("/auth-check")
    public ResponseEntity<String> checkAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User Authorities: " + auth.getAuthorities());

        return ResponseEntity.ok("Checked roles, see console log.");
    }
}
