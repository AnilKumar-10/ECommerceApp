package com.ECommerceApp.ServiceImplementation;

import com.ECommerceApp.Model.User.RolePermission;
import com.ECommerceApp.Repository.RolePermissionRepository;
import com.ECommerceApp.Util.SecurityUtils;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
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
                        if (targetId == null) return false; // can't check without ID
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
