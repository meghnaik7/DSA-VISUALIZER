package com.dsavisualizer.controller;

import com.dsavisualizer.dto.history.LearningHistoryCreateRequest;
import com.dsavisualizer.dto.history.LearningHistoryResponse;

import com.dsavisualizer.enums.LearningStatus;

import com.dsavisualizer.service.LearningHistoryService;
import com.dsavisualizer.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-history")
@RequiredArgsConstructor
public class LearningHistoryController {

    private final LearningHistoryService learningHistoryService;
    private final UserService userService;

    // Start learning without an associated artifact
    @PostMapping
    public ResponseEntity<LearningHistoryResponse> startLearning(
            @Valid @RequestBody LearningHistoryCreateRequest request,
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        LearningHistoryResponse response = learningHistoryService.startLearning(
                userId,
                null,
                request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Start learning with an algorithm artifact
    @PostMapping("/artifacts/{artifactId}")
    public ResponseEntity<LearningHistoryResponse> startArtifactLearning(
            @PathVariable Long artifactId,
            @Valid @RequestBody LearningHistoryCreateRequest request,
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        LearningHistoryResponse response = learningHistoryService.startLearning(
                userId,
                artifactId,
                request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Complete a learning session
    @PatchMapping("/{historyId}/complete")
    public ResponseEntity<LearningHistoryResponse> completeLearning(
            @PathVariable Long historyId,
            @RequestParam @PositiveOrZero Integer timeSpentSeconds,
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        LearningHistoryResponse response = learningHistoryService.completeLearning(
                userId,
                historyId,
                timeSpentSeconds);

        return ResponseEntity.ok(response);
    }

    // Mark a learning session as failed
    @PatchMapping("/{historyId}/fail")
    public ResponseEntity<LearningHistoryResponse> markLearningFailed(
            @PathVariable Long historyId,
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        LearningHistoryResponse response = learningHistoryService.markLearningFailed(
                userId,
                historyId);

        return ResponseEntity.ok(response);
    }

    // Get all learning history of logged-in user
    @GetMapping("/my")
    public ResponseEntity<List<LearningHistoryResponse>> getMyLearningHistory(
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        List<LearningHistoryResponse> history = learningHistoryService.getUserLearningHistory(userId);

        return ResponseEntity.ok(history);
    }

    // Get learning history by status
    @GetMapping("/my/status/{status}")
    public ResponseEntity<List<LearningHistoryResponse>> getMyHistoryByStatus(
            @PathVariable LearningStatus status,
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        List<LearningHistoryResponse> history = learningHistoryService.getUserLearningHistoryByStatus(
                userId,
                status);

        return ResponseEntity.ok(history);
    }

    // Get authenticated user's database ID
    private Long getAuthenticatedUserId(Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("User is not authenticated");
        }

        return userService.getUserIdByEmail(authentication.getName());
    }
}