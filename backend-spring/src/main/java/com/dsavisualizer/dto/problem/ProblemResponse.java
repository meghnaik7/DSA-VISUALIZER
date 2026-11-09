package com.dsavisualizer.dto.problem;

import com.dsavisualizer.enums.Difficulty;
import java.time.LocalDateTime;

public record ProblemResponse(

        Long id,
        String title,
        String slug,
        String statement,
        String inputDescription,
        String outputDescription,
        String constraints,
        Difficulty difficulty,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}
