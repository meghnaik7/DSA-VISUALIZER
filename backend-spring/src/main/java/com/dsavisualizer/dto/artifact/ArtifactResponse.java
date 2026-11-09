package com.dsavisualizer.dto.artifact;

import com.dsavisualizer.enums.ArtifactStatus;
import com.dsavisualizer.enums.ArtifactType;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;

public record ArtifactResponse(

        Long id,
        Long algorithmId,
        Long problemId,
        ArtifactType artifactType,
        String cacheKey,
        String approach,
        String pseudocode,
        String timeComplexity,
        String spaceComplexity,
        JsonNode visualizationSpec,
        String visualizationLibrary,
        String libraryVersion,
        ArtifactStatus generationStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}
