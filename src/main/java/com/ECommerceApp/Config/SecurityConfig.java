package com.ECommerceApp.Config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.LinkedHashMap;
import java.util.Map;
@Slf4j
@Configuration
public class SecurityConfig {
//    @Autowired
//    private CustomUserDetailsService detailsService;
//
//    @Autowired
//    private JwtFilter jwtFilter;

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////        System.out.println("inside the SecurityFilterChain");
//        log.info("inside the SecurityFilterChain");
//        return http.csrf(csrf -> csrf.disable())
//                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                                .requestMatchers("/auth/**").permitAll()
////                                .requestMatchers("/auth/student/register").permitAll()
//                                .requestMatchers("/student/**").hasRole("STUDENT")
//                                .requestMatchers("/faculty/**").hasAnyRole("HOD", "TEACHER", "PRINCIPAL")
//                                .anyRequest().authenticated()
//                )
//                .exceptionHandling(ex -> ex
//                        .accessDeniedHandler((request, response, accessDeniedException) -> {
//                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                            response.setContentType("application/json");
//                            Map<String, Object> body = new LinkedHashMap<>();
//                            body.put("timestamp", java.time.ZonedDateTime.now().toString());
//                            body.put("status", 403);
//                            body.put("error", "Forbidden");
//                            body.put("message", "Access Denied");
//                            body.put("path", request.getRequestURI());
//                            new ObjectMapper().writeValue(response.getOutputStream(), body);
//                        })
//                )
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }
//
//@Bean
//public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    http
//            .csrf(AbstractHttpConfigurer::disable)
//            .authorizeHttpRequests(auth -> auth
//                    .requestMatchers("/auth/**").permitAll()
//                    .requestMatchers("/admin/**").hasRole("ADMIN")
//                    .requestMatchers("/seller/**").hasAnyRole("SELLER", "ADMIN")
//                    .requestMatchers("/delivery/**").hasAnyRole("DELIVERY_PERSON", "ADMIN")
//                    .requestMatchers("/user/**", "/products/**", "/orders/**").hasAnyRole("USER", "SELLER", "ADMIN")
//                    .anyRequest().authenticated()
//            )
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .authenticationProvider(authenticationProvider())
//            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//    return http.build();
//}
//
//
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        log.info("inside the PasswordEncoder");
//        return  new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        log.info("inside the Authentication Manager ");
//        return  configuration.getAuthenticationManager();
//    }
}

