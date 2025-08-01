package com.ECommerceApp.ServiceImplementation;

import com.ECommerceApp.Model.User.RolePermission;
import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.Repository.RolePermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service("permissionService")
public class PermissionService {

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    public boolean hasPermission(String resource, String action) {

        log.info("Checking the permissions:  resource: "+resource+"    action: "+action);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .toList();
        log.info("role  are: "+roles);
        if(roles.contains(Users.Role.ADMIN.name())){
            return true;
        }
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleIn(roles);
        log.info("role permissions are: "+rolePermissions);
        return rolePermissions.stream().anyMatch(rp ->
                rp.getPermissions().stream().anyMatch(p ->
                        p.getResource().equalsIgnoreCase(resource)  &&  p.getAction().equalsIgnoreCase(action)
                )
        );
    }
}
/*

    LIST ROLES AND THEIR PERMISSIONS.
 *{
  "role": "ADMIN",
  "permissions": [
    { "resource": "*", "action": "*" }
  ]
}

*   {
  "role": "BUYER",
  "permissions": [
    { "resource": "CART", "action": "INSERT" },
    { "resource": "WISHLIST", "action": "INSERT" },
    { "resource": "ORDER", "action": "INSERT" },
    { "resource": "REVIEW", "action": "INSERT" },
    { "resource": "EXCHANGE", "action": "INSERT" },
    { "resource": "RETURN", "action": "INSERT" },
    { "resource": "USER", "action": "INSERT" },
    { "resource": "PAYMENT", "action": "INSERT" },

    { "resource": "ORDER", "action": "READ" },
    { "resource": "SHIPPING", "action": "READ" },
    { "resource": "TAX", "action": "READ" },
    { "resource": "INVOICE", "action": "READ" },
    { "resource": "COUPON", "action": "READ" },
    { "resource": "REVIEW", "action": "READ" },
    { "resource": "EXCHANGE", "action": "READ" },
    { "resource": "RETURN", "action": "READ" },
    { "resource": "USER", "action": "READ" },
    { "resource": "WISHLIST", "action": "READ" },
    { "resource": "CART", "action": "READ" },

    { "resource": "USER", "action": "UPDATE" },
    { "resource": "REVIEW", "action": "UPDATE" },
    { "resource": "CART", "action": "UPDATE" },
    { "resource": "WISHLIST", "action": "UPDATE" },

    { "resource": "CART", "action": "DELETE" },
    { "resource": "WISHLIST", "action": "DELETE" },
    { "resource": "REVIEW", "action": "DELETE" }
  ]
}

*
* {
  "role": "DELIVERY",
  "permissions": [
    { "resource": "PAYMENT", "action": "INSERT" },

    { "resource": "ORDER", "action": "READ" },
    { "resource": "SHIPPING", "action": "READ" },
    { "resource": "PAYMENT", "action": "READ" },
    { "resource": "EXCHANGE", "action": "READ" },
    { "resource": "RETURN", "action": "READ" },
    { "resource": "DELIVERY", "action": "READ" },

    { "resource": "EXCHANGE", "action": "UPDATE" },
    { "resource": "RETURN", "action": "UPDATE" },
    { "resource": "PAYMENT", "action": "UPDATE" },
    { "resource": "SHIPPING", "action": "UPDATE" },
    { "resource": "DELIVERY", "action": "UPDATE" }
  ]
}

*
* {
  "role": "SELLER",
  "permissions": [
    { "resource": "PRODUCT", "action": "INSERT" },
    { "resource": "STOCK", "action": "INSERT" },
    { "resource": "COUPON", "action": "INSERT" },

    { "resource": "ORDER", "action": "READ" },
    { "resource": "TAX", "action": "READ" },
    { "resource": "COUPON", "action": "READ" },
    { "resource": "PRODUCT", "action": "READ" },
    { "resource": "REVIEW", "action": "READ" },
    { "resource": "ADDRESS", "action": "READ" },
    { "resource": "STOCK", "action": "READ" },
    { "resource": "SHIPPING", "action": "READ" },
    { "resource": "EXCHANGE", "action": "READ" },
    { "resource": "RETURN", "action": "READ" },
    { "resource": "INVOICE", "action": "READ" },
    { "resource": "USER", "action": "READ" },

    { "resource": "PRODUCT", "action": "DELETE" },
    { "resource": "STOCK", "action": "DELETE" },

    { "resource": "PRODUCT", "action": "UPDATE" },
    { "resource": "STOCK", "action": "UPDATE" },
    { "resource": "COUPON", "action": "UPDATE" }
  ]
}

 */
