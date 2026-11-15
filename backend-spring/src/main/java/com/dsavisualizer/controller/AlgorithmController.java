package com.dsavisualizer.controller;

import com.dsavisualizer.dto.algorithm.AlgorithmCreateRequest;
import com.dsavisualizer.dto.algorithm.AlgorithmResponse;
import com.dsavisualizer.dto.algorithm.AlgorithmUpdateRequest;
import com.dsavisualizer.service.AlgorithmService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/algorithms")
@RequiredArgsConstructor
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    // 1. Create a new algorithm
    @PostMapping
    public ResponseEntity<AlgorithmResponse> createAlgorithm(
            @Valid @RequestBody AlgorithmCreateRequest request) {

        AlgorithmResponse response = algorithmService.createAlgorithm(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 2. Get all algorithms
    @GetMapping
    public ResponseEntity<List<AlgorithmResponse>> getAllAlgorithms() {

        List<AlgorithmResponse> algorithms = algorithmService.getAllAlgorithms();

        return ResponseEntity.ok(algorithms);
    }

    // 3. Get algorithm by ID
    @GetMapping("/{id}")
    public ResponseEntity<AlgorithmResponse> getAlgorithmById(
            @PathVariable Long id) {

        AlgorithmResponse response = algorithmService.getAlgorithmById(id);

        return ResponseEntity.ok(response);
    }

    // 4. Get algorithm by slug
    @GetMapping("/slug/{slug}")
    public ResponseEntity<AlgorithmResponse> getAlgorithmBySlug(
            @PathVariable String slug) {

        AlgorithmResponse response = algorithmService.getAlgorithmBySlug(slug);

        return ResponseEntity.ok(response);
    }

    // 5. Update algorithm
    @PatchMapping("/{id}")
    public ResponseEntity<AlgorithmResponse> updateAlgorithm(
            @PathVariable Long id,
            @Valid @RequestBody AlgorithmUpdateRequest request) {

        AlgorithmResponse response = algorithmService.updateAlgorithm(id, request);

        return ResponseEntity.ok(response);
    }

    // 6. Deactivate algorithm
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateAlgorithm(
            @PathVariable Long id) {

        algorithmService.deactivateAlgorithm(id);

        return ResponseEntity.noContent().build();
    }
}