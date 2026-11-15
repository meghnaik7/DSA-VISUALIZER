package com.dsavisualizer.service;

import com.dsavisualizer.dto.problem.ProblemCreateRequest;
import com.dsavisualizer.dto.problem.ProblemUpdateRequest;
import com.dsavisualizer.dto.problem.ProblemResponse;

import com.dsavisualizer.entity.Problem;

import com.dsavisualizer.repository.ProblemRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemService {

    private final ProblemRepository problemRepository;


    // 1. Create a new problem
    public ProblemResponse createProblem(ProblemCreateRequest request) {

        // Check whether slug already exists
        if (problemRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException(
                    "Problem with this slug already exists"
            );
        }

        // Convert DTO into Entity
        Problem problem = Problem.builder()
                .title(request.title())
                .slug(request.slug())
                .statement(request.statement())
                .inputDescription(request.inputDescription())
                .outputDescription(request.outputDescription())
                .constraints(request.constraints())
                .difficulty(request.difficulty())
                .build();

        // Save problem
        Problem savedProblem = problemRepository.save(problem);

        return mapToResponse(savedProblem);
    }


    // 2. Get all problems
    @Transactional(readOnly = true)
    public List<ProblemResponse> getAllProblems() {

        return problemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // 3. Get problem by ID
    @Transactional(readOnly = true)
    public ProblemResponse getProblemById(Long id) {

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Problem not found with ID: " + id
                ));

        return mapToResponse(problem);
    }


    // 4. Get problem by slug
    @Transactional(readOnly = true)
    public ProblemResponse getProblemBySlug(String slug) {

        Problem problem = problemRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Problem not found with slug: " + slug
                ));

        return mapToResponse(problem);
    }


    // 5. Update problem
    public ProblemResponse updateProblem(
            Long id,
            ProblemUpdateRequest request
    ) {

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Problem not found with ID: " + id
                ));

        // Update title
        if (request.title() != null) {
            problem.setTitle(request.title());
        }

        // Update slug
        if (request.slug() != null) {

            if (!request.slug().equals(problem.getSlug())
                    && problemRepository.existsBySlug(request.slug())) {

                throw new IllegalArgumentException(
                        "Problem slug already exists"
                );
            }

            problem.setSlug(request.slug());
        }

        // Update statement
        if (request.statement() != null) {
            problem.setStatement(request.statement());
        }

        // Update input description
        if (request.inputDescription() != null) {
            problem.setInputDescription(request.inputDescription());
        }

        // Update output description
        if (request.outputDescription() != null) {
            problem.setOutputDescription(request.outputDescription());
        }

        // Update constraints
        if (request.constraints() != null) {
            problem.setConstraints(request.constraints());
        }

        // Update difficulty
        if (request.difficulty() != null) {
            problem.setDifficulty(request.difficulty());
        }

        Problem updatedProblem = problemRepository.save(problem);

        return mapToResponse(updatedProblem);
    }


    // Convert Entity into Response DTO
    private ProblemResponse mapToResponse(Problem problem) {

        return new ProblemResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getSlug(),
                problem.getStatement(),
                problem.getInputDescription(),
                problem.getOutputDescription(),
                problem.getConstraints(),
                problem.getDifficulty(),
                problem.getCreatedAt(),
                problem.getUpdatedAt()
        );
    }

}