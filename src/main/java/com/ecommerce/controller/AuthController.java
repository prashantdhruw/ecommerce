package com.ecommerce.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecommerce.dto.AuthResponse;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.SignupRequest;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
   private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @org.springframework.web.bind.annotation.RequestBody SignupRequest signUpRequest) {
        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        if (userRepository.findAll().stream().anyMatch(u -> u.getEmail().equals(signUpRequest.getEmail()))) {
            return ResponseEntity.badRequest().body("Email is already in use!");
        }

        // Only allow CUSTOMER role via public signup
        if (!"CUSTOMER".equalsIgnoreCase(signUpRequest.getRole())) {
            return ResponseEntity.status(403).body("Only CUSTOMER role can be created via public signup.");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        // Dynamically assign role from request, prepending "ROLE_"
        user.setRoles("ROLE_" + signUpRequest.getRole().toUpperCase());

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @Operation(summary = "Authenticate user and get JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateToken((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal());

            return ResponseEntity.ok(new AuthResponse(jwt));
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            log.warn("Login attempt failed for email {}: User not found", loginRequest.getEmail());
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            log.warn("Login attempt failed for email {}: Bad credentials", loginRequest.getEmail());
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (org.springframework.security.core.AuthenticationException e) {
            log.error("Authentication failed unexpectedly for email {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body("Authentication failed");
        } catch (Exception e) {
            log.error("Unexpected error during login for email {}: {}", loginRequest.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred during login.");
        }
    }
}