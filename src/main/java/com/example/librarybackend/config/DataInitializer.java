package com.example.librarybackend.config;

import com.example.librarybackend.entity.*;
import com.example.librarybackend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            if (!userRepo.existsByUsername("admin")) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPasswordHash(encoder.encode("123456"));
                admin.setRole(Role.ADMIN);
                userRepo.save(admin);
            }
        };
    }
}