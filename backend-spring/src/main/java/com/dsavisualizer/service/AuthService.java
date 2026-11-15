package com.dsavisualizer.service;

import com.dsavisualizer.dto.auth.AuthResponse;
import com.dsavisualizer.dto.auth.LoginRequest;
import com.dsavisualizer.dto.auth.UserRegisterRequest;
import com.dsavisualizer.dto.auth.UserResponse;

import com.dsavisualizer.entity.User;
import com.dsavisualizer.enums.UserRole;

import com.dsavisualizer.repository.UserRepository;
import com.dsavisualizer.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    // Register a new user
    @Transactional
    public AuthResponse register(UserRegisterRequest request) {

        // 1. Normalize email
        String email = normalizeEmail(request.email());

        // 2. Check whether email already exists
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists");
        }

        // 3. Create user entity
        User user = User.builder()
                .name(request.name().trim())
                .email(email)
                .passwordHash(
                        passwordEncoder.encode(request.password()))
                .role(UserRole.USER)
                .isActive(true)
                .build();

        // 4. Save user in database
        User savedUser = userRepository.save(user);

        // 5. Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());

        // 6. Generate JWT
        String token = jwtService.generateToken(userDetails);

        // 7. Return authentication response
        return createAuthResponse(savedUser, token);
    }

    // Login an existing user
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        // 1. Normalize email
        String email = normalizeEmail(request.email());

        // 2. Authenticate email and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.password()));

        // 3. Find authenticated user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 4. Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // 5. Generate JWT
        String token = jwtService.generateToken(userDetails);

        // 6. Return authentication response
        return createAuthResponse(user, token);
    }

    // Build authentication response
    private AuthResponse createAuthResponse(
            User user,
            String token) {

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getIsActive(),
                user.getCreatedAt());

        return new AuthResponse(
                userResponse,
                token,
                "Bearer");
    }

    // Normalize email
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}