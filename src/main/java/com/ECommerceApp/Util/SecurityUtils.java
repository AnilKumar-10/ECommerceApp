package com.ECommerceApp.Util;

import com.ECommerceApp.ServiceImplementation.UserDetailService.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public  String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        return customUserDetails.getUserId();
    }

    public  String getCurrentUserMail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
