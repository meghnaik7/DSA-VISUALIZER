package com.dsavisualizer.dto.algorithm;

import com.dsavisualizer.enums.Difficulty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AlgorithmUpdateRequest(

        @Size(min = 1, max = 150)
        String name,

        @Size(min = 1, max = 180)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
        String slug,

        @Size(min = 1, max = 100)
        String category,

        @Size(min = 1, max = 10000)
        String description,

        @Size(min = 1, max = 100)
        String inputType,

        @Size(min = 1, max = 100)
        String outputType,

        Difficulty difficulty

) {}
