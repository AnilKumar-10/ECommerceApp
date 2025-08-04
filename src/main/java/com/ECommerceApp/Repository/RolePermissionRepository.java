package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.User.RolePermission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends MongoRepository<RolePermission, String> {

    @Query("{ 'role': { $in: ?0 }, 'permissions': { $elemMatch: { 'resource': ?1, 'action': ?2 } } }")
    boolean existsByRolesAndPermission(List<String> roles, String resource, String action);

    // Optional: For full permission check logic
    List<RolePermission> findByRoleIn(List<String> roles);

    RolePermission findByRole(String role);

}
