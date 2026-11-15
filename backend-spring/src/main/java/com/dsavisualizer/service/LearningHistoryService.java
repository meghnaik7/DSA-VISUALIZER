package com.dsavisualizer.service;

import com.dsavisualizer.dto.history.LearningHistoryCreateRequest;
import com.dsavisualizer.dto.history.LearningHistoryResponse;

import com.dsavisualizer.entity.AlgorithmArtifact;
import com.dsavisualizer.entity.LearningHistory;
import com.dsavisualizer.entity.User;

import com.dsavisualizer.enums.LearningStatus;

import com.dsavisualizer.repository.AlgorithmArtifactRepository;
import com.dsavisualizer.repository.LearningHistoryRepository;
import com.dsavisualizer.repository.UserRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LearningHistoryService {

    private final LearningHistoryRepository learningHistoryRepository;
    private final UserRepository userRepository;
    private final AlgorithmArtifactRepository algorithmArtifactRepository;
    private final ObjectMapper objectMapper;

    // Start a new learning session
    @Transactional
    public LearningHistoryResponse startLearning(
            Long userId,
            Long artifactId,
            LearningHistoryCreateRequest request) {

        // 1. Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2. Check whether user is active
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new IllegalArgumentException(
                    "Inactive users cannot start learning sessions");
        }

        // 3. Find algorithm artifact if provided
        AlgorithmArtifact artifact = null;

        if (artifactId != null) {
            artifact = algorithmArtifactRepository.findById(artifactId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Algorithm artifact not found"));
        }

        // 4. Convert JsonNode into Map
        Map<String, Object> inputData = null;

        if (request.inputData() != null &&
                !request.inputData().isNull()) {

            inputData = objectMapper.convertValue(
                    request.inputData(),
                    new TypeReference<Map<String, Object>>() {
                    });
        }

        // 5. Create learning history
        LearningHistory history = LearningHistory.builder()
                .user(user)
                .algorithmArtifact(artifact)
                .originalQuery(request.originalQuery().trim())
                .requestType(request.requestType())
                .inputData(inputData)
                .status(LearningStatus.STARTED)
                .build();

        // 6. Save history
        LearningHistory savedHistory = learningHistoryRepository.save(history);

        // 7. Return response
        return mapToResponse(savedHistory);
    }

    // Mark learning session as completed
    @Transactional
    public LearningHistoryResponse completeLearning(
            Long userId,
            Long historyId,
            Integer timeSpentSeconds) {

        // 1. Find learning history
        LearningHistory history = findUserHistory(userId, historyId);

        // 2. Validate time spent
        if (timeSpentSeconds == null || timeSpentSeconds < 0) {
            throw new IllegalArgumentException(
                    "Time spent must be zero or greater");
        }

        // 3. Check current status
        if (history.getStatus() != LearningStatus.STARTED) {
            throw new IllegalArgumentException(
                    "Only started learning sessions can be completed");
        }

        // 4. Update history
        history.setStatus(LearningStatus.COMPLETED);
        history.setTimeSpentSeconds(timeSpentSeconds);
        history.setCompletedAt(LocalDateTime.now());

        // 5. Save and return
        return mapToResponse(
                learningHistoryRepository.save(history));
    }

    // Mark learning session as failed
    @Transactional
    public LearningHistoryResponse markLearningFailed(
            Long userId,
            Long historyId) {

        LearningHistory history = findUserHistory(userId, historyId);

        if (history.getStatus() != LearningStatus.STARTED) {
            throw new IllegalArgumentException(
                    "Only started learning sessions can be marked as failed");
        }

        history.setStatus(LearningStatus.FAILED);

        return mapToResponse(
                learningHistoryRepository.save(history));
    }

    // Get all learning histories of a user
    @Transactional(readOnly = true)
    public List<LearningHistoryResponse> getUserLearningHistory(
            Long userId) {

        return learningHistoryRepository.findByUser_Id(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get learning histories by status
    @Transactional(readOnly = true)
    public List<LearningHistoryResponse> getUserLearningHistoryByStatus(
            Long userId,
            LearningStatus status) {

        return learningHistoryRepository
                .findByUser_IdAndStatus(userId, status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Find a learning history belonging to a particular user
    private LearningHistory findUserHistory(
            Long userId,
            Long historyId) {

        LearningHistory history = learningHistoryRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Learning history not found"));

        if (!history.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    "You cannot access another user's learning history");
        }

        return history;
    }

    // Convert entity into response DTO
    private LearningHistoryResponse mapToResponse(
            LearningHistory history) {

        return new LearningHistoryResponse(
                history.getId(),
                history.getAlgorithmArtifact() != null
                        ? history.getAlgorithmArtifact().getId()
                        : null,
                history.getOriginalQuery(),
                history.getRequestType(),
                history.getStatus(),
                history.getTimeSpentSeconds(),
                history.getCompletedAt(),
                history.getCreatedAt());
    }
}