package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.*;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends MongoRepository<Users,String> {
    Optional<Users> findByEmail(String email);
    List<Users> findByRolesContaining(String role);
}
