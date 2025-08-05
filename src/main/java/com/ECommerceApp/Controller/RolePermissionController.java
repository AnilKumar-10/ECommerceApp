package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.User.RolePermission;
import com.ECommerceApp.ServiceImplementation.UserDetailService.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RolePermissionController {
    @Autowired
    private RolePermissionService rolePermissionService;

    @PostMapping("/createRole")
    public ResponseEntity<?> addRole(@RequestBody RolePermission rolePermission) {
        RolePermission saved = rolePermissionService.createRolePermission(rolePermission);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/updateRole")
    public ResponseEntity<?> updateRole(@RequestBody RolePermission rolePermission) {
        RolePermission saved = rolePermissionService.updateRolePermission(rolePermission);
        return ResponseEntity.ok(saved);
    }
}
