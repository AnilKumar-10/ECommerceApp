package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.User.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends MongoRepository<Users,String> {
    Optional<Users> findByEmail(String email);
    List<Users> findByRolesContaining(String role);

    @Query("{'roles':'SELLER'}")
    List<Users> findByRolesContainingSellerRole();

    // Count users whose roles array contains "SELLER"
    long countByRolesContaining(String role);

    // Count all users
    long count();



}
