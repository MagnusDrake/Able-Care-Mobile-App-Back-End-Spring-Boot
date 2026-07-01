package com.example.demo.config;

import com.example.demo.Entity.TherapyType;
import com.example.demo.Entity.User;
import com.example.demo.Entity.UserRole;
import com.example.demo.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {


        return args -> {
            // Only seed the data if the user's table is completely empty.
            // This prevents duplicate entries if the server restarts while the database is still running.
            if (userRepository.count() == 0) {

                // 1. Create an Admin User (e.g., the project owner)
                User admin = new User();
                admin.setFirstName("Lynn");
                admin.setLastName("Peier");
                admin.setEmail("lynn.admin@ablecare.com");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setRole(UserRole.ADMIN);
                // Admins don't need specialty or zipCode

                // 2. Create an available Physical Therapist in zip 80918
                User pt1 = new User();
                pt1.setFirstName("Sarah");
                pt1.setLastName("Connor");
                pt1.setEmail("sarah.pt@ablecare.com");
                pt1.setPasswordHash(passwordEncoder.encode("therapist123"));
                pt1.setRole(UserRole.THERAPIST);
                pt1.setSpecialty(TherapyType.PT);
                pt1.setZipCode("80918");
                pt1.setAvailable(true);

                // 3. Create an available Occupational Therapist in zip 80918
                User ot1 = new User();
                ot1.setFirstName("John");
                ot1.setLastName("Watson");
                ot1.setEmail("john.ot@ablecare.com");
                ot1.setPasswordHash(passwordEncoder.encode("therapist123"));
                ot1.setRole(UserRole.THERAPIST);
                ot1.setSpecialty(TherapyType.OT);
                ot1.setZipCode("80918");
                ot1.setAvailable(true);

                // 4. Create an UNAVAILABLE Physical Therapist in zip 80920 (Great for testing the filter)
                User pt2 = new User();
                pt2.setFirstName("Jane");
                pt2.setLastName("Doe");
                pt2.setEmail("jane.pt@ablecare.com");
                pt2.setPasswordHash(passwordEncoder.encode("therapist123"));
                pt2.setRole(UserRole.THERAPIST);
                pt2.setSpecialty(TherapyType.PT);
                pt2.setZipCode("80920");
                pt2.setAvailable(false); // The dispatch query should filter this user out

                // Save all to PostgreSQL
                userRepository.saveAll(List.of(admin, pt1, ot1, pt2));

                System.out.println("Database successfully seeded with initial Able Care staff!");
            } else {
                System.out.println("Database already contains data. Skipping seeding process.");
            }
        };
    }
}
