package com.ECommerceApp.ServiceImplementation;

import com.ECommerceApp.Model.Delivery.DeliveryPerson;
import com.ECommerceApp.Model.User.Users;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    @Getter
    private String userId;
    private  String username;
    private  String password;
    @Getter
    private  Date passwordChangedAt;
    private  Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Users user) {
        this.userId = user.getId();
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.passwordChangedAt = user.getPasswordChangedAt();
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    // add constructor for DeliveryPerson too if needed
    public CustomUserDetails(DeliveryPerson dp) {
        this.userId = dp.getId();
        this.username = dp.getEmail();
        this.password = dp.getPassword();
        this.passwordChangedAt = dp.getPasswordChangedAt();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_DELIVERY"));
    }

    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return password; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    // other UserDetails methods like isAccountNonLocked etc.
}
