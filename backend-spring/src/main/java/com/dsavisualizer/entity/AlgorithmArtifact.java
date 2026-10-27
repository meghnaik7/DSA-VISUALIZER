package com.dsavisualizer.entity;

import com.dsavisualizer.enums.ArtifactStatus;
import com.dsavisualizer.enums.ArtifactType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "algorithm_artifacts", indexes = {
        @Index(name = "idx_artifact_cache_key", columnList = "cacheKey", unique = true),
        @Index(name = "idx_artifact_type", columnList = "artifactType"),
        @Index(name = "idx_artifact_algorithm", columnList = "algorithm_id"),
        @Index(name = "idx_artifact_problem", columnList = "problem_id"),
        @Index(name = "idx_artifact_status", columnList = "generationStatus")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlgorithmArtifact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Associated algorithm; nullable for problem-only artifacts.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "algorithm_id")
    private Algorithm algorithm;

    /**
     * Associated problem; nullable for generic algorithm artifacts.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ArtifactType artifactType;

    @NotBlank(message = "Cache key is required")
    @Size(max = 255)
    @Column(nullable = false, unique = true, length = 255)
    private String cacheKey;

    @Column(columnDefinition = "TEXT")
    private String approach;

    @Column(columnDefinition = "TEXT")
    private String pseudocode;

    @Size(max = 50)
    @Column(length = 50)
    private String timeComplexity;

    @Size(max = 50)
    @Column(length = 50)
    private String spaceComplexity;

    /**
     * Structured visualization data (nodes, edges, traversal order, animation steps).
     * Stored as JSONB in PostgreSQL for efficient querying.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> visualizationSpec;

    @Column(columnDefinition = "TEXT")
    private String sourceCode;

    @Size(max = 100)
    @Column(length = 100)
    private String visualizationLibrary;

    @Size(max = 30)
    @Column(length = 30)
    private String libraryVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ArtifactStatus generationStatus = ArtifactStatus.PENDING;

    @Size(max = 64)
    @Column(length = 64)
    private String contentHash;

    @Size(max = 50)
    @Column(length = 50)
    private String promptVersion;

    @Size(max = 100)
    @Column(length = 100)
    private String modelName;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ── Relationships ──────────────────────────────────────────────────

    @OneToMany(mappedBy = "algorithmArtifact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LearningHistory> learningHistories = new ArrayList<>();
}
