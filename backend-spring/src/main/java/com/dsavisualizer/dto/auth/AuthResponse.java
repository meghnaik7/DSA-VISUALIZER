package com.dsavisualizer.dto.auth;

public record AuthResponse(

        UserResponse user,
        String accessToken,
        String tokenType

) {}