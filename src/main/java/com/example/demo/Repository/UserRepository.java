package com.example.demo.Repository;

import com.example.demo.Entity.TherapyType;
import com.example.demo.Entity.User;
import com.example.demo.Entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used for login and authentication
    Optional<User> findByEmail(String email);

    // The Dispatch Query: Finds available therapists of a specific type in the target zip code
    List<User> findByRoleAndIsAvailableTrueAndSpecialtyAndZipCode(
            UserRole role,
            TherapyType specialty,
            String zipCode
    );
}