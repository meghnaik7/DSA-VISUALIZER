package com.dsavisualizer.service;

import com.dsavisualizer.dto.artifact.ArtifactCreateRequest;
import com.dsavisualizer.dto.artifact.ArtifactResponse;

import com.dsavisualizer.entity.Algorithm;
import com.dsavisualizer.entity.AlgorithmArtifact;
import com.dsavisualizer.entity.Problem;

import com.dsavisualizer.enums.ArtifactStatus;
import com.dsavisualizer.enums.ArtifactType;

import com.dsavisualizer.repository.AlgorithmArtifactRepository;
import com.dsavisualizer.repository.AlgorithmRepository;
import com.dsavisualizer.repository.ProblemRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AlgorithmArtifactService {

    private final AlgorithmArtifactRepository artifactRepository;

    private final AlgorithmRepository algorithmRepository;

    private final ProblemRepository problemRepository;

    private final ObjectMapper objectMapper;


    // 1. Create a new artifact
    public ArtifactResponse createArtifact(ArtifactCreateRequest request) {

        // Validate artifact relationship
        if (request.artifactType() == ArtifactType.ALGORITHM
                && request.algorithmId() == null) {

            throw new IllegalArgumentException(
                    "Algorithm ID is required for an algorithm artifact"
            );
        }

        if (request.artifactType() == ArtifactType.PROBLEM_SOLUTION
                && request.problemId() == null) {

            throw new IllegalArgumentException(
                    "Problem ID is required for a problem solution artifact"
            );
        }

        // Check whether cache key already exists
        if (artifactRepository.existsByCacheKey(request.cacheKey())) {

            throw new IllegalArgumentException(
                    "Artifact with this cache key already exists"
            );
        }

        // Retrieve associated algorithm
        Algorithm algorithm = null;

        if (request.algorithmId() != null) {

            algorithm = algorithmRepository.findById(request.algorithmId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Algorithm not found: " + request.algorithmId()
                    ));
        }

        // Retrieve associated problem
        Problem problem = null;

        if (request.problemId() != null) {

            problem = problemRepository.findById(request.problemId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Problem not found: " + request.problemId()
                    ));
        }

        // Convert JSON specification into Map
        Map<String, Object> visualizationSpec =
                objectMapper.convertValue(
                        request.visualizationSpec(),
                        new TypeReference<Map<String, Object>>() {}
                );

        // Convert request DTO into Entity
        AlgorithmArtifact artifact = AlgorithmArtifact.builder()
                .algorithm(algorithm)
                .problem(problem)
                .artifactType(request.artifactType())
                .cacheKey(request.cacheKey())
                .approach(request.approach())
                .pseudocode(request.pseudocode())
                .timeComplexity(request.timeComplexity())
                .spaceComplexity(request.spaceComplexity())
                .visualizationSpec(visualizationSpec)
                .sourceCode(request.sourceCode())
                .visualizationLibrary(request.visualizationLibrary())
                .libraryVersion(request.libraryVersion())
                .promptVersion(request.promptVersion())
                .modelName(request.modelName())
                .generationStatus(ArtifactStatus.READY)
                .build();

        // Save artifact
        AlgorithmArtifact savedArtifact =
                artifactRepository.save(artifact);

        return mapToResponse(savedArtifact);
    }


    // 2. Get artifact by ID
    @Transactional(readOnly = true)
    public ArtifactResponse getArtifactById(Long id) {

        AlgorithmArtifact artifact = artifactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Artifact not found with ID: " + id
                ));

        return mapToResponse(artifact);
    }


    // 3. Get READY artifact using cache key
    @Transactional(readOnly = true)
    public ArtifactResponse getArtifactByCacheKey(String cacheKey) {

        AlgorithmArtifact artifact = artifactRepository
                .findByCacheKeyAndGenerationStatus(
                        cacheKey,
                        ArtifactStatus.READY
                )
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ready artifact not found for cache key: " + cacheKey
                ));

        return mapToResponse(artifact);
    }


    // 4. Get artifacts belonging to an algorithm
    @Transactional(readOnly = true)
    public List<ArtifactResponse> getArtifactsByAlgorithm(Long algorithmId) {

        if (!algorithmRepository.existsById(algorithmId)) {
            throw new EntityNotFoundException(
                    "Algorithm not found: " + algorithmId
            );
        }

        return artifactRepository.findByAlgorithm_Id(algorithmId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // 5. Get artifacts belonging to a problem
    @Transactional(readOnly = true)
    public List<ArtifactResponse> getArtifactsByProblem(Long problemId) {

        if (!problemRepository.existsById(problemId)) {
            throw new EntityNotFoundException(
                    "Problem not found: " + problemId
            );
        }

        return artifactRepository.findByProblem_Id(problemId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // Convert Entity into Response DTO
    private ArtifactResponse mapToResponse(AlgorithmArtifact artifact) {

        JsonNode visualizationJson =
                objectMapper.valueToTree(artifact.getVisualizationSpec());

        return new ArtifactResponse(
                artifact.getId(),

                artifact.getAlgorithm() != null
                        ? artifact.getAlgorithm().getId()
                        : null,

                artifact.getProblem() != null
                        ? artifact.getProblem().getId()
                        : null,

                artifact.getArtifactType(),
                artifact.getCacheKey(),
                artifact.getApproach(),
                artifact.getPseudocode(),
                artifact.getTimeComplexity(),
                artifact.getSpaceComplexity(),
                visualizationJson,
                artifact.getVisualizationLibrary(),
                artifact.getLibraryVersion(),
                artifact.getGenerationStatus(),
                artifact.getCreatedAt(),
                artifact.getUpdatedAt()
        );
    }

}