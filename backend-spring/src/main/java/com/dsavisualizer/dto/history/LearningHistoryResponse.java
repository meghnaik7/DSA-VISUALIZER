package com.dsavisualizer.dto.history;

import com.dsavisualizer.enums.LearningStatus;
import com.dsavisualizer.enums.RequestType;
import java.time.LocalDateTime;

public record LearningHistoryResponse(

        Long id,
        Long algorithmArtifactId,
        String originalQuery,
        RequestType requestType,
        LearningStatus status,
        Integer timeSpentSeconds,
        LocalDateTime completedAt,
        LocalDateTime createdAt

) {}
