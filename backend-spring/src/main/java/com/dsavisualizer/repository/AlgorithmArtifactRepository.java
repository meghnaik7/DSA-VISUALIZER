package com.dsavisualizer.repository;

import com.dsavisualizer.entity.AlgorithmArtifact;
import com.dsavisualizer.enums.ArtifactStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlgorithmArtifactRepository
        extends JpaRepository<AlgorithmArtifact, Long> {

    // Find an artifact using its unique cache key
    Optional<AlgorithmArtifact> findByCacheKey(String cacheKey);

    // Find an artifact only if it is READY
    Optional<AlgorithmArtifact> findByCacheKeyAndGenerationStatus(
            String cacheKey,
            ArtifactStatus generationStatus
    );

    // Check whether an artifact with this cache key exists
    boolean existsByCacheKey(String cacheKey);

    // Find all artifacts associated with an algorithm
    List<AlgorithmArtifact> findByAlgorithm_Id(Long algorithmId);

    // Find artifacts by their generation status
    List<AlgorithmArtifact> findByGenerationStatus(
            ArtifactStatus generationStatus
    );

}