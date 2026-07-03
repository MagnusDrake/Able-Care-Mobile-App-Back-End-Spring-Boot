package com.example.demo.Service;

import com.example.demo.Entity.TherapyType;
import com.example.demo.Entity.User;
import com.example.demo.Entity.UserRole;
import com.example.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Logic to find exactly who should receive the notification
    public List<User> getAvailableTherapistsForDispatch(TherapyType specialty, String zipCode) {
        return userRepository.findByRoleAndIsAvailableTrueAndSpecialtyAndZipCode(
                UserRole.THERAPIST,
                specialty,
                zipCode
        );
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    //Admin can view all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //Admin can update user details
    public User updateUser(Long id, User updatedDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update the fields (excluding ID, password, and creation date for security)
        existingUser.setFirstName(updatedDetails.getFirstName());
        existingUser.setLastName(updatedDetails.getLastName());
        existingUser.setEmail(updatedDetails.getEmail());
        existingUser.setRole(updatedDetails.getRole());
        existingUser.setSpecialty(updatedDetails.getSpecialty());
        existingUser.setZipCode(updatedDetails.getZipCode());
        existingUser.setAvailable(updatedDetails.isAvailable());

        return userRepository.save(existingUser);
    }

    // Add user logic
    public User addUser(User newUser) {
        // Prototype logic: Set a default password if one isn't provided
        if (newUser.getPasswordHash() == null || newUser.getPasswordHash().isEmpty()) {
            newUser.setPasswordHash(passwordEncoder.encode("password123"));
        }else {
            newUser.setPasswordHash(passwordEncoder.encode(newUser.getPasswordHash()));
        }
        return userRepository.save(newUser);
    }
