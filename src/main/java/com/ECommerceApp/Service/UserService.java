package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.UserRegistrationRequest;
import com.ECommerceApp.Exceptions.UserNotFoundException;
import com.ECommerceApp.Model.Users;
import com.ECommerceApp.Repository.UsersRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UsersRepository  usersRepository;

    @Autowired
    private ProductService productService;

    public Users registerUser(UserRegistrationRequest user) {
        Users users = new Users();
        BeanUtils.copyProperties(user,users);
        validateUserForRoles(users);
        user.setActive(true);
        user.setCreatedAt(new Date());
        return usersRepository.save(users);
    }

    public String registerUsers(List<UserRegistrationRequest> users){
        int c=0;
        for(UserRegistrationRequest user:users){
            usersRepository.save(registerUser(user));
            c++;
        }
        return "users registration is done: "+c;
    }


    // 2. Update user profile based on roles
    public Users updateUser(String userId, Users updatedData) {
        Users existing = usersRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Update common fields
        existing.setName(updatedData.getName());
        existing.setPhone(updatedData.getPhone());
        existing.setGender(updatedData.getGender());

        // If role contains SELLER, update seller-specific fields
        if (Arrays.asList(existing.getRoles()).contains("SELLER")) {
            existing.setShopName(updatedData.getShopName());
            existing.setShopDescription(updatedData.getShopDescription());
            existing.setShippingOptions(updatedData.getShippingOptions());
            existing.setAssignedZones(updatedData.getAssignedZones());
        }

        return usersRepository.save(existing);
    }

    // 3. Deactivate user
    public void deactivateUser(String userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setActive(false);
        usersRepository.save(user);
    }

    // 4. Get user by ID
    public Users getUserById(String id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    // 5. Get user by email
    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    // 6. Get all users with a specific role
    public List<Users> getUsersByRole(String role) {
        return usersRepository.findByRolesContaining(role.toUpperCase());
    }

    // 7. Add new role to existing user (e.g., buyer becomes seller), This can be done my only ADMIN
    public Users addRoleToUser(String userId, String newRole) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Set<String> roles = new HashSet<>(Arrays.asList(user.getRoles()));
        roles.add(newRole.toUpperCase());

        user.setRoles(roles.toArray(new String[0]));

        // If becoming a seller, ensure seller fields are present
        if (newRole.equalsIgnoreCase("SELLER")) {
            validateSellerFields(user);
        }

        return usersRepository.save(user);
    }

    // Validate required fields of Users
    private void validateUserForRoles(Users user) {
        List<String> roles = Arrays.asList(user.getRoles());
        if (roles.contains("SELLER")) {
            user.setRating(0.0);
            validateSellerFields(user);
        }
        else {
            // If User is NOT a seller, clear seller-specific fields
            user.setRating(null);
            user.setShopName(null);
            user.setShopDescription(null);
            user.setShippingOptions(null);
            user.setAssignedZones(null);
        }

    }

    private void validateSellerFields(Users user) {
        if (user.getShopName() == null || user.getShopName().isEmpty()) {
            throw new RuntimeException("Shop name is required for sellers");
        }

        if (user.getShippingOptions() == null || user.getShippingOptions().isEmpty()) {
            throw new RuntimeException("At least one shipping option is required for sellers");
        }

        if (user.getAssignedZones() == null || user.getAssignedZones().isEmpty()) {
            throw new RuntimeException("At least one assigned zone is required for sellers");
        }
    }


    public List<Users> getAllUsers(){
        return usersRepository.findAll();
    }


}
