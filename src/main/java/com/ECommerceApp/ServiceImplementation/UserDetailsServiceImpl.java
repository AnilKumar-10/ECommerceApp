package com.ECommerceApp.ServiceImplementation;

import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.User.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private  UserService userService;
    @Autowired
    private DeliveryService deliveryService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try to load user by email (for BUYER, SELLER, ADMIN)
        log.info("inside uds: "+email);
        Optional<Users> userOpt = userService.loadUserByMail(email);
        if (userOpt.isPresent()) {
            return new CustomUserDetails(userOpt.get());
        }

        // Else try delivery person
        Optional<DeliveryPerson> dpOpt = deliveryService.loadDeliveryByMail(email);
        if (dpOpt.isPresent()) {
            return new CustomUserDetails(dpOpt.get());
        }

        throw new UsernameNotFoundException("User not found with mail: " + email);
    }
}
