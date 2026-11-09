package com.dsavisualizer.dto.algorithm;

import com.dsavisualizer.enums.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AlgorithmCreateRequest(

        @NotBlank(message = "Algorithm name is required")
        @Size(max = 150)
        String name,

        @NotBlank(message = "Slug is required")
        @Size(max = 180)
        @Pattern(
                regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "Slug must contain lowercase letters, numbers and hyphens"
        )
        String slug,

        @NotBlank(message = "Category is required")
        @Size(max = 100)
        String category,

        @NotBlank(message = "Description is required")
        @Size(max = 10000)
        String description,

        @NotBlank(message = "Input type is required")
        @Size(max = 100)
        String inputType,

        @NotBlank(message = "Output type is required")
        @Size(max = 100)
        String outputType,

        @NotNull(message = "Difficulty is required")
        Difficulty difficulty

) {}
