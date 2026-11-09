package com.dsavisualizer.dto.visualization;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VisualizationRequest(

        @NotBlank(message = "Query is required")
        @Size(max = 10000)
        String query,

        JsonNode inputData,

        @Size(max = 50)
        String preferredLibrary,

        boolean includeExplanation,

        boolean includePseudocode

) {}
