package com.dsavisualizer.dto.artifact;

import com.dsavisualizer.enums.ArtifactType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ArtifactCreateRequest(

        Long algorithmId,

        Long problemId,

        @NotNull(message = "Artifact type is required")
        ArtifactType artifactType,

        @NotBlank(message = "Cache key is required")
        @Size(max = 128)
        @Pattern(regexp = "^[a-fA-F0-9]{64}$",
                message = "Cache key must be a SHA-256 hexadecimal hash")
        String cacheKey,

        @NotBlank(message = "Approach is required")
        @Size(max = 50000)
        String approach,

        @Size(max = 30000)
        String pseudocode,

        @NotBlank(message = "Time complexity is required")
        @Size(max = 100)
        String timeComplexity,

        @NotBlank(message = "Space complexity is required")
        @Size(max = 100)
        String spaceComplexity,

        @NotNull(message = "Visualization specification is required")
        JsonNode visualizationSpec,

        @Size(max = 100000)
        String sourceCode,

        @NotBlank(message = "Visualization library is required")
        @Size(max = 100)
        String visualizationLibrary,

        @Size(max = 50)
        String libraryVersion,

        @Size(max = 100)
        String promptVersion,

        @Size(max = 100)
        String modelName

) {}
