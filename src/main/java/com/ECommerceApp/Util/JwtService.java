package com.ECommerceApp.Util;

import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.ServiceImplementation.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
@Slf4j
@Service
public class JwtService {

    private static final String SECRET_KEY = "mySuperSecretKey12345678901234567890123456789012";
    private final long EXPIRATION = 1000 * 60 * 60;

    private Key getSignInKey() {
        log.info("inside the getSignKey()");
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(UserDetails userDetails) {

        if (!(userDetails instanceof CustomUserDetails customUser)) {
            throw new IllegalArgumentException("Unsupported user type");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", getRolesFromAuthorities(customUser.getAuthorities()));
        claims.put("pwdChangedAt", customUser.getPasswordChangedAt().getTime());
        claims.put("userId",customUser.getUserId());
        return buildToken(claims, userDetails);
    }

    private String buildToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSignInKey())
                .compact();
    }


    private <T> T extractClaims(String token, Function<Claims,T> resolver) {
        log.info("inside the ExtractClaims()");
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return resolver.apply(claims);
    }

    public String extractUsername(String token){
        log.info("inside the ExtractUsername");
        return extractClaims(token, Claims::getSubject);
    }


    public String extractPassword(String token) {
        log.info("inside the extractPasswordHash");
        return extractClaims(token, claims -> claims.get("pwd", String.class));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("inside the isTokenValid()");
        String username = extractUsername(token);
        Long pwdChangedAtToken = extractClaims(token, claims -> claims.get("pwdChangedAt", Long.class)); // this is present in token

        if (!(userDetails instanceof CustomUserDetails)) {
            return false;
        }

        Date pwdChangedAtInDB = ((CustomUserDetails) userDetails).getPasswordChangedAt(); //this is taken from the db.
        boolean isPwdMatch = pwdChangedAtInDB != null && pwdChangedAtInDB.getTime() == pwdChangedAtToken;

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token) && isPwdMatch;
    }


    private boolean isTokenExpired(String token) {
        log.info("inside the isTokenExpired()");
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    public List<Users.Role> extractRoles(String token) {
        return extractClaims(token, claims -> (List<Users.Role>) claims.get("roles"));
    }

    private List<String> getRolesFromAuthorities(Collection<?> authorities) {
        List<String> roles = new ArrayList<>();
        authorities.forEach(auth -> roles.add(auth.toString()));
        log.info("roles in jwt service: "+roles);
        return roles;
    }


}
