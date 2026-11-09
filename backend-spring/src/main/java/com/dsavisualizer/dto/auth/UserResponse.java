package com.dsavisualizer.dto.auth;

import com.dsavisualizer.enums.UserRole;
import java.time.LocalDateTime;

public record UserResponse(

        Long id,
        String name,
        String email,
        UserRole role,
        Boolean isActive,
        LocalDateTime createdAt

) {}
