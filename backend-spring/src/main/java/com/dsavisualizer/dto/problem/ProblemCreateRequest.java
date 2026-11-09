package com.dsavisualizer.dto.problem;

import com.dsavisualizer.enums.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProblemCreateRequest(

        @NotBlank(message = "Problem title is required")
        @Size(max = 200)
        String title,

        @NotBlank(message = "Problem slug is required")
        @Size(max = 220)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
        String slug,

        @NotBlank(message = "Problem statement is required")
        @Size(max = 50000)
        String statement,

        @Size(max = 10000)
        String inputDescription,

        @Size(max = 10000)
        String outputDescription,

        @Size(max = 20000)
        String constraints,

        @NotNull(message = "Difficulty is required")
        Difficulty difficulty

) {}
