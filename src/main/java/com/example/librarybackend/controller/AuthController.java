package com.example.librarybackend.controller;

import com.example.librarybackend.dto.LoginRequest;
import com.example.librarybackend.dto.LoginResponse;
import com.example.librarybackend.entity.AppUser;
import com.example.librarybackend.repository.UserRepository;
import com.example.librarybackend.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.username(),
                            req.password()
                    )
            );

            // ✅ Lấy username từ authentication (không cast nữa)
            String username = auth.getName();

            // ✅ Query lại AppUser từ DB
            AppUser user = userRepository
                    .findByUsername(username)
                    .orElseThrow();

            String token = jwtService.generateToken(
                    user.getUsername(),
                    user.getRole().name()
            );

            return new LoginResponse(
                    token,
                    user.getUsername(),
                    user.getRole().name()
            );

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Sai username hoặc password");
        }
    }
}