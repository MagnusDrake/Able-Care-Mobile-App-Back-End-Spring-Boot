package com.example.demo.Controller;

import com.example.demo.Entity.TherapyType;
import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;
import com.example.demo.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Endpoint to find who should get the notification based on area and specialty
    @GetMapping("/dispatch-targets")
    public ResponseEntity<List<User>> getAvailableTherapists(
            @RequestParam TherapyType specialty,
            @RequestParam String zipCode) {

        List<User> availableTherapists = userService.getAvailableTherapistsForDispatch(specialty, zipCode);
        return ResponseEntity.ok(availableTherapists);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Prototype check: In a production app, use BCrypt to compare hashed passwords!
            if (passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    //Admin can view all users
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    //Admin can update user details
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    // Admin can add a new user
    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody User newUser) {
        return ResponseEntity.ok(userService.addUser(newUser));
    }
}