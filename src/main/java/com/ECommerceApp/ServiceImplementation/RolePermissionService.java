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
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    public RolePermission createRolePermission(RolePermission rolePermission) {
        rolePermission.setId(String.valueOf(sequenceGeneratorService.getNextSequence("roleId")));
        return rolePermissionRepository.save(rolePermission);
    }
}
