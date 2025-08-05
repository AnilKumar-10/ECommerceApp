package com.ECommerceApp.ServiceImplementation.UserDetailService;

import com.ECommerceApp.Model.User.RolePermission;
import com.ECommerceApp.Repository.User.RolePermissionRepository;
import com.ECommerceApp.ServiceImplementation.Order.SequenceGeneratorService;
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

    public RolePermission updateRolePermission(RolePermission rolePermission){
        return rolePermissionRepository.save(rolePermission);
    }
}
