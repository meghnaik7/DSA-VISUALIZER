package com.dsavisualizer.dto.problem;

import com.dsavisualizer.enums.Difficulty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProblemUpdateRequest(

        @Size(min = 1, max = 200)
        String title,

        @Size(min = 1, max = 220)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
        String slug,

        @Size(min = 1, max = 50000)
        String statement,

        @Size(max = 10000)
        String inputDescription,

        @Size(max = 10000)
        String outputDescription,

        @Size(max = 20000)
        String constraints,

        Difficulty difficulty

) {}
