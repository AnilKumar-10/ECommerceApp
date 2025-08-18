package com.ECommerceApp.Util;

import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.ServiceImplementation.User.UserService;
import com.ECommerceApp.ServiceInterface.Order.IOrderService;
import com.ECommerceApp.ServiceInterface.User.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class OwnershipGuard {
    @Autowired
    private UserServiceInterface userService;

    public void checkSelf(String ownerId) {
        String currentUserId = new SecurityUtils().getCurrentUserId();
        if (!currentUserId.equals(ownerId)) {
            checkAdmin();
            throw new AccessDeniedException("Access denied: not your data.");
        }
    }

    public void checkAdmin(){
        String currentUserId = new SecurityUtils().getCurrentUserId();
        Users user  = userService.getUserById(currentUserId);
        if(!user.getRoles().contains("ADMIN")){
            throw new AccessDeniedException("Access denied");
        }
    }
}