package com.ECommerceApp.Config;

import com.ECommerceApp.ServiceImplementation.UserDetailsServiceImpl;
import com.ECommerceApp.Util.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private  UserDetailsServiceImpl userDetailsService;
    @Autowired
    private  JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("http: "+http);
        System.out.println("Inside SecurityFilterChain");
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/auth/**", "/browse/**").permitAll()

                        // Delivery person access
                        .requestMatchers("/delivery/**", "/shipping/**", "/exchange/**", "/return/**", "/address/**")
                        .hasAnyRole("DELIVERY_PERSON", "ADMIN")

                        // Seller access
                        .requestMatchers("/product/**", "/stockLog/**", "/address/**")
                        .hasAnyRole("SELLER", "ADMIN")

                        // User access
                        .requestMatchers("/order/**", "/payment/**", "/invoice/**", "/review/**",
                                "/exchange/**", "/return/**", "/address/**", "/cart/**","/cart", "/wish/**").hasAnyRole("USER", "ADMIN")

                        // All roles access `/user/**`
                        .requestMatchers("/user/**").hasAnyRole("ADMIN", "USER", "SELLER", "DELIVERY_PERSON")

                        // All others require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // use strength param if needed
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}

