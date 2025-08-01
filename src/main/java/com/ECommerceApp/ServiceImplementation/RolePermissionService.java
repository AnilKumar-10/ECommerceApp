package com.ECommerceApp.ServiceImplementation;

import com.ECommerceApp.Model.User.RolePermission;
import com.ECommerceApp.Repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolePermissionService {
    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    public RolePermission createRolePermission(RolePermission rolePermission) {
        return rolePermissionRepository.save(rolePermission);
    }
}
