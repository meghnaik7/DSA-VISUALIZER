package com.dsavisualizer.dto.history;

import com.dsavisualizer.enums.RequestType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LearningHistoryCreateRequest(

        @NotBlank(message = "Original query is required")
        @Size(max = 10000)
        String originalQuery,

        @NotNull(message = "Request type is required")
        RequestType requestType,

        JsonNode inputData

) {}
