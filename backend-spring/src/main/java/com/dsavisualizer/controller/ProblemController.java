package com.dsavisualizer.controller;

import com.dsavisualizer.dto.problem.ProblemCreateRequest;
import com.dsavisualizer.dto.problem.ProblemUpdateRequest;
import com.dsavisualizer.dto.problem.ProblemResponse;
import com.dsavisualizer.service.ProblemService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    // 1. Create a new problem
    @PostMapping
    public ResponseEntity<ProblemResponse> createProblem(
            @Valid @RequestBody ProblemCreateRequest request) {

        ProblemResponse response = problemService.createProblem(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 2. Get all problems
    @GetMapping
    public ResponseEntity<List<ProblemResponse>> getAllProblems() {

        List<ProblemResponse> problems = problemService.getAllProblems();

        return ResponseEntity.ok(problems);
    }

    // 3. Get problem by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProblemResponse> getProblemById(
            @PathVariable Long id) {

        ProblemResponse response = problemService.getProblemById(id);

        return ResponseEntity.ok(response);
    }

    // 4. Get problem by slug
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProblemResponse> getProblemBySlug(
            @PathVariable String slug) {

        ProblemResponse response = problemService.getProblemBySlug(slug);

        return ResponseEntity.ok(response);
    }

    // 5. Update problem
    @PatchMapping("/{id}")
    public ResponseEntity<ProblemResponse> updateProblem(
            @PathVariable Long id,
            @Valid @RequestBody ProblemUpdateRequest request) {

        ProblemResponse response = problemService.updateProblem(id, request);

        return ResponseEntity.ok(response);
    }
}