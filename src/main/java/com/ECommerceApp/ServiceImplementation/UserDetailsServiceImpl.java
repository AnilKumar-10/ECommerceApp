package com.ECommerceApp.ServiceImplementation;

import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.User.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private  UserService userService;
    @Autowired
    private DeliveryService deliveryService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try to load user by email (for BUYER, SELLER, ADMIN)
        Users userOpt = userService.getUserByEmail(email);
        if (userOpt!=null) {
            return new CustomUserDetails(userOpt);
        }

        // Else try delivery person
        DeliveryPerson dpOpt = deliveryService.getDeliverryPersonByEmail(email);
        if (dpOpt!=null) {
            return new CustomUserDetails(dpOpt);
        }

        throw new UsernameNotFoundException("User not found with identifier: " + email);
    }
}
