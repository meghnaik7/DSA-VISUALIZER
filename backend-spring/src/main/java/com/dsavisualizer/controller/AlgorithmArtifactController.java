package com.dsavisualizer.controller;

import com.dsavisualizer.dto.artifact.ArtifactCreateRequest;
import com.dsavisualizer.dto.artifact.ArtifactResponse;
import com.dsavisualizer.service.AlgorithmArtifactService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artifacts")
@RequiredArgsConstructor
public class AlgorithmArtifactController {

    private final AlgorithmArtifactService artifactService;

    // 1. Create a new artifact
    @PostMapping
    public ResponseEntity<ArtifactResponse> createArtifact(
            @Valid @RequestBody ArtifactCreateRequest request) {

        ArtifactResponse response = artifactService.createArtifact(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 2. Get artifact by ID
    @GetMapping("/{id}")
    public ResponseEntity<ArtifactResponse> getArtifactById(
            @PathVariable Long id) {

        ArtifactResponse response = artifactService.getArtifactById(id);

        return ResponseEntity.ok(response);
    }

    // 3. Get READY artifact using cache key
    @GetMapping("/cache/{cacheKey}")
    public ResponseEntity<ArtifactResponse> getArtifactByCacheKey(
            @PathVariable String cacheKey) {

        ArtifactResponse response = artifactService.getArtifactByCacheKey(cacheKey);

        return ResponseEntity.ok(response);
    }

    // 4. Get artifacts belonging to an algorithm
    @GetMapping("/algorithm/{algorithmId}")
    public ResponseEntity<List<ArtifactResponse>> getArtifactsByAlgorithm(
            @PathVariable Long algorithmId) {

        List<ArtifactResponse> artifacts = artifactService.getArtifactsByAlgorithm(algorithmId);

        return ResponseEntity.ok(artifacts);
    }

    // 5. Get artifacts belonging to a problem
    @GetMapping("/problem/{problemId}")
    public ResponseEntity<List<ArtifactResponse>> getArtifactsByProblem(
            @PathVariable Long problemId) {

        List<ArtifactResponse> artifacts = artifactService.getArtifactsByProblem(problemId);

        return ResponseEntity.ok(artifacts);
    }
}