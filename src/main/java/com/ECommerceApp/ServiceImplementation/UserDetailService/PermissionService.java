package com.ECommerceApp.ServiceImplementation.UserDetailService;

import com.ECommerceApp.Model.User.RolePermission;
import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.Repository.User.RolePermissionRepository;
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

        log.info("Checking the permissions:  resource: " + resource + "    action: " + action);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .toList();
        log.info("role  are: " + roles);
        if (roles.contains(Users.Role.ADMIN.name())) {
            return true;
        }
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleIn(roles);
        log.info("role permissions are: " + rolePermissions);
        return rolePermissions.stream().anyMatch(rp ->
                rp.getPermissions().stream().anyMatch(p ->
                        p.getResource().equalsIgnoreCase(resource) && p.getAction().equalsIgnoreCase(action)
                )
        );
    }
}


