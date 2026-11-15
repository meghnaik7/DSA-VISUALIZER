package com.dsavisualizer.service;

import com.dsavisualizer.dto.algorithm.AlgorithmCreateRequest;
import com.dsavisualizer.dto.algorithm.AlgorithmResponse;
import com.dsavisualizer.dto.algorithm.AlgorithmUpdateRequest;
import com.dsavisualizer.entity.Algorithm;
import com.dsavisualizer.enums.AlgorithmCategory;
import com.dsavisualizer.repository.AlgorithmRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class AlgorithmService {

    private final AlgorithmRepository algorithmRepository;


    // 1. Create a new algorithm
    public AlgorithmResponse createAlgorithm(AlgorithmCreateRequest request) {

        // Check whether slug already exists
        if (algorithmRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException(
                    "Algorithm with this slug already exists"
            );
        }

        // Convert category String into enum
        AlgorithmCategory category = parseCategory(request.category());

        // Convert DTO into Entity
        Algorithm algorithm = Algorithm.builder()
                .name(request.name())
                .slug(request.slug())
                .category(category)
                .description(request.description())
                .inputType(request.inputType())
                .outputType(request.outputType())
                .difficulty(request.difficulty())
                .isActive(true)
                .build();

        // Save entity
        Algorithm savedAlgorithm = algorithmRepository.save(algorithm);

        // Return response DTO
        return mapToResponse(savedAlgorithm);
    }

    // 2. Get all algorithms
    @Transactional(readOnly = true)
    public List<AlgorithmResponse> getAllAlgorithms() {

        return algorithmRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 3. Get algorithm by ID
    @Transactional(readOnly = true)
    public AlgorithmResponse getAlgorithmById(Long id) {

        Algorithm algorithm = algorithmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Algorithm not found with ID: " + id
                ));

        return mapToResponse(algorithm);
    }


    // 4. Get algorithm by slug
    @Transactional(readOnly = true)
    public AlgorithmResponse getAlgorithmBySlug(String slug) {

        Algorithm algorithm = algorithmRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Algorithm not found with slug: " + slug
                ));

        return mapToResponse(algorithm);
    }


    // 5. Update algorithm
    public AlgorithmResponse updateAlgorithm(
            Long id,
            AlgorithmUpdateRequest request
    ) {

        Algorithm algorithm = algorithmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Algorithm not found with ID: " + id
                ));

        // Update name if provided
        if (request.name() != null) {
            algorithm.setName(request.name());
        }

        // Update slug if provided
        if (request.slug() != null) {

            if (!request.slug().equals(algorithm.getSlug())
                    && algorithmRepository.existsBySlug(request.slug())) {

                throw new IllegalArgumentException(
                        "Algorithm slug already exists"
                );
            }

            algorithm.setSlug(request.slug());
        }

        // Update category if provided
        if (request.category() != null) {
            algorithm.setCategory(parseCategory(request.category()));
        }

        // Update description if provided
        if (request.description() != null) {
            algorithm.setDescription(request.description());
        }

        // Update input type if provided
        if (request.inputType() != null) {
            algorithm.setInputType(request.inputType());
        }

        // Update output type if provided
        if (request.outputType() != null) {
            algorithm.setOutputType(request.outputType());
        }

        // Update difficulty if provided
        if (request.difficulty() != null) {
            algorithm.setDifficulty(request.difficulty());
        }

        Algorithm updatedAlgorithm = algorithmRepository.save(algorithm);

        return mapToResponse(updatedAlgorithm);
    }

    // 6. Deactivate algorithm
    public void deactivateAlgorithm(Long id) {

        Algorithm algorithm = algorithmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Algorithm not found with ID: " + id
                ));

        algorithm.setIsActive(false);

        algorithmRepository.save(algorithm);
    }

    // Convert category String into AlgorithmCategory enum
    private AlgorithmCategory parseCategory(String category) {

        try {
            String normalizedCategory = category
                    .trim()
                    .replace('-', '_')
                    .replace(' ', '_')
                    .toUpperCase(Locale.ROOT);

            return AlgorithmCategory.valueOf(normalizedCategory);

        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "Invalid algorithm category: " + category
            );
        }
    }


    // Convert Entity into Response DTO
    private AlgorithmResponse mapToResponse(Algorithm algorithm) {

        return new AlgorithmResponse(
                algorithm.getId(),
                algorithm.getName(),
                algorithm.getSlug(),
                algorithm.getCategory().name(),
                algorithm.getDescription(),
                algorithm.getInputType(),
                algorithm.getOutputType(),
                algorithm.getDifficulty(),
                algorithm.getIsActive(),
                algorithm.getCreatedAt(),
                algorithm.getUpdatedAt()
        );
    }

}