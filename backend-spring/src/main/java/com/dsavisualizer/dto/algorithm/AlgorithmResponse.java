package com.dsavisualizer.dto.algorithm;

import com.dsavisualizer.enums.Difficulty;
import java.time.LocalDateTime;

public record AlgorithmResponse(

        Long id,
        String name,
        String slug,
        String category,
        String description,
        String inputType,
        String outputType,
        Difficulty difficulty,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}
