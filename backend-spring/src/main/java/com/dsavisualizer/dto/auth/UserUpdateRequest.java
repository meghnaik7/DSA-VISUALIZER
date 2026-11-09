package com.dsavisualizer.dto.auth;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

        @Size(min = 1, max = 100,
                message = "Name must contain between 1 and 100 characters")
        String name

) {}
