package com.ECommerceApp.ServiceImplementation.UserDetailService;

import com.ECommerceApp.Model.User.RolePermission;
import com.ECommerceApp.Repository.User.RolePermissionRepository;
import com.ECommerceApp.Util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
@Slf4j
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        log.info("inside the has permission1:");
        // Used when you call: hasPermission('RESOURCE', 'ACTION')
        String resource = targetDomainObject != null ? targetDomainObject.toString().toUpperCase() : null;
        String action = permission.toString().toUpperCase();
        List<String> roles = getRolesFromAuthentication(authentication);
        log.info("resource: "+resource+"  action: "+action+"  roles: "+roles);
        return hasPermission(roles, resource, action, null, authentication);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        // Used when you call: hasPermission(#id, 'com.Model.ClassName', 'ACTION')
        log.info("inside the has permission2:");
        String resource = extractResourceFromType(targetType);
        String action = permission.toString().toUpperCase();
        List<String> roles = getRolesFromAuthentication(authentication);
        String currentUserId = new SecurityUtils().getCurrentUserId();
        log.info("resource: "+resource+"  action: "+action+"  roles: "+roles);
        return hasPermission(roles, resource, action, targetId.toString(), authentication);
    }

    private boolean hasPermission(List<String> roles, String resource, String action, String targetId, Authentication auth) {
        String currentUserId = new SecurityUtils().getCurrentUserId();

        for (String role : roles) {
            RolePermission rolePermission = rolePermissionRepository.findByRole(role);
            if (rolePermission == null || rolePermission.getPermissions() == null) continue;

            for (RolePermission.Permission p : rolePermission.getPermissions()) {
                boolean resourceMatch = "*".equals(p.getResource()) || p.getResource().equalsIgnoreCase(resource);
                boolean actionMatch = "*".equals(p.getAction()) || p.getAction().equalsIgnoreCase(action);

                if (resourceMatch && actionMatch) {
                    String scope = p.getScope() != null ? p.getScope().toUpperCase() : "ALL";

                    //  Allow if scope is ALL
                    if ("ALL".equals(scope)) return true;

                    //  Allow if SELF and user owns the resource (by ID match)
                    if ("SELF".equals(scope)) {
                        if (targetId == null) {
                            log.info("SELF scope without ID: assuming ownership will be checked in service layer.");
                            return true;
                        }
                        return targetId.equals(currentUserId);
                    }
                }
            }
        }

        return false;
    }

    private List<String> getRolesFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.replace("ROLE_", "")) // cleanup
                    .toList();
        }
        return List.of();
    }

    private String extractResourceFromType(String targetType) {
        String[] parts = targetType.split("\\.");
        return parts.length > 0 ? parts[parts.length - 1].toUpperCase() : targetType.toUpperCase();
    }
}



/*
[
  {
    "id": "PERM1",
    "role": "ADMIN",
    "permissions": [
      { "resource": "*", "action": "*", "scope": "ALL" }
    ]
  },
  {
    "id": "PERM2",
    "role": "USER",
    "permissions": [
      { "resource": "CART", "action": "INSERT", "scope": "SELF" },
      { "resource": "WISHLIST", "action": "INSERT", "scope": "SELF" },
      { "resource": "ORDER", "action": "INSERT", "scope": "SELF" },
      { "resource": "REVIEW", "action": "INSERT", "scope": "SELF" },
      { "resource": "EXCHANGE", "action": "INSERT", "scope": "SELF" },
      { "resource": "RETURN", "action": "INSERT", "scope": "SELF" },
      { "resource": "USER", "action": "INSERT", "scope": "SELF" },
      { "resource": "PAYMENT", "action": "INSERT", "scope": "SELF" },

      { "resource": "ORDER", "action": "READ", "scope": "SELF" },
      { "resource": "SHIPPING", "action": "READ", "scope": "SELF" },
      { "resource": "TAX", "action": "READ", "scope": "ALL" },
      { "resource": "INVOICE", "action": "READ", "scope": "ALL" },
      { "resource": "COUPON", "action": "READ", "scope": "ALL" },
      { "resource": "REVIEW", "action": "READ", "scope": "ALL" },
      { "resource": "EXCHANGE", "action": "READ", "scope": "SELF" },
      { "resource": "RETURN", "action": "READ", "scope": "SELF" },
      { "resource": "USER", "action": "READ", "scope": "SELF" },
      { "resource": "WISHLIST", "action": "READ", "scope": "SELF" },
      { "resource": "CART", "action": "READ", "scope": "SELF" },

      { "resource": "USER", "action": "UPDATE", "scope": "SELF" },
      { "resource": "REVIEW", "action": "UPDATE", "scope": "SELF" },
      { "resource": "CART", "action": "UPDATE", "scope": "SELF" },
      { "resource": "WISHLIST", "action": "UPDATE", "scope": "SELF" },

      { "resource": "CART", "action": "DELETE", "scope": "SELF" },
      { "resource": "WISHLIST", "action": "DELETE", "scope": "SELF" },
      { "resource": "REVIEW", "action": "DELETE", "scope": "SELF" }
    ]
  },
  {
    "id": "PERM3",
    "role": "SELLER",
    "permissions": [
      { "resource": "PRODUCT", "action": "INSERT", "scope": "SELF" },
      { "resource": "STOCK", "action": "INSERT", "scope": "SELF" },
      { "resource": "COUPON", "action": "INSERT", "scope": "SELF" },

      { "resource": "ORDER", "action": "READ", "scope": "ALL" },
      { "resource": "TAX", "action": "READ", "scope": "ALL" },
      { "resource": "COUPON", "action": "READ", "scope": "SELF" },
      { "resource": "PRODUCT", "action": "READ", "scope": "SELF" },
      { "resource": "REVIEW", "action": "READ", "scope": "ALL" },
      { "resource": "ADDRESS", "action": "READ", "scope": "ALL" },
      { "resource": "STOCK", "action": "READ", "scope": "SELF" },
      { "resource": "SHIPPING", "action": "READ", "scope": "ALL" },
      { "resource": "EXCHANGE", "action": "READ", "scope": "ALL" },
      { "resource": "RETURN", "action": "READ", "scope": "ALL" },
      { "resource": "INVOICE", "action": "READ", "scope": "ALL" },
      { "resource": "USER", "action": "READ", "scope": "ALL" },

      { "resource": "PRODUCT", "action": "DELETE", "scope": "SELF" },
      { "resource": "STOCK", "action": "DELETE", "scope": "SELF" },

      { "resource": "STOCK", "action": "UPDATE", "scope": "SELF" },
      { "resource": "PRODUCT", "action": "UPDATE", "scope": "SELF" },
      { "resource": "COUPON", "action": "UPDATE", "scope": "SELF" }
    ]
  },
  {
    "id": "PERM4",
    "role": "DELIVERY",
    "permissions": [
      { "resource": "PAYMENT", "action": "INSERT", "scope": "ALL" },

      { "resource": "ORDER", "action": "READ", "scope": "ALL" },
      { "resource": "SHIPPING", "action": "READ", "scope": "ALL" },
      { "resource": "PAYMENT", "action": "READ", "scope": "ALL" },
      { "resource": "EXCHANGE", "action": "READ", "scope": "ALL" },
      { "resource": "RETURN", "action": "READ", "scope": "ALL" },
      { "resource": "DELIVERY", "action": "READ", "scope": "SELF" },

      { "resource": "EXCHANGE", "action": "UPDATE", "scope": "ALL" },
      { "resource": "RETURN", "action": "UPDATE", "scope": "ALL" },
      { "resource": "PAYMENT", "action": "UPDATE", "scope": "ALL" },
      { "resource": "SHIPPING", "action": "UPDATE", "scope": "ALL" },
      { "resource": "DELIVERY", "action": "UPDATE", "scope": "SELF" }
    ]
  }
]

 */