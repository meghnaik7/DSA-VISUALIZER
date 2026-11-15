package com.dsavisualizer.service;

import com.dsavisualizer.dto.auth.UserResponse;
import com.dsavisualizer.dto.auth.UserUpdateRequest;
import com.dsavisualizer.entity.User;
import com.dsavisualizer.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;


    // 1. Get all users
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // 2. Get user by ID
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with ID: " + id
                ));

        return mapToResponse(user);
    }


    // 3. Get all active users
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {

        return userRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // 4. Update user profile
    public UserResponse updateUser(
            Long id,
            UserUpdateRequest request
    ) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with ID: " + id
                ));

        // Update name if provided
        if (request.name() != null) {
            user.setName(request.name().trim());
        }

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }


    // 5. Deactivate user account
    public void deactivateUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with ID: " + id
                ));

        user.setIsActive(false);

        userRepository.save(user);
    }


    // 6. Get user ID by email
    @Transactional(readOnly = true)
    public Long getUserIdByEmail(String email) {

        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with email: " + email
                ));
    }


    // 7. Get user by email
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with email: " + email
                ));

        return mapToResponse(user);
    }


    // Convert User Entity into UserResponse DTO
    private UserResponse mapToResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getIsActive(),
                user.getCreatedAt()
        );
    }

}