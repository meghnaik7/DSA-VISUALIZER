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
        @Index(name = "idx_artifact_algo_status", columnList = "algorithm_id, generation_status"),
        @Index(name = "idx_artifact_problem", columnList = "problem_id")
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
    @Column(name = "artifact_type", nullable = false, length = 30)
    private ArtifactType artifactType;

    @NotBlank(message = "Cache key is required")
    @Size(max = 255)
    @Column(name = "cache_key", nullable = false, unique = true, length = 255)
    private String cacheKey;

    @Column(columnDefinition = "TEXT")
    private String approach;

    @Column(columnDefinition = "TEXT")
    private String pseudocode;

    @Size(max = 50)
    @Column(name = "time_complexity", length = 50)
    private String timeComplexity;

    @Size(max = 50)
    @Column(name = "space_complexity", length = 50)
    private String spaceComplexity;

    /**
     * Structured visualization data (nodes, edges, traversal order, animation steps).
     * Stored as JSONB in PostgreSQL for efficient querying.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "visualization_spec", columnDefinition = "jsonb")
    private Map<String, Object> visualizationSpec;

    @Column(name = "source_code", columnDefinition = "TEXT")
    private String sourceCode;

    @Size(max = 100)
    @Column(name = "visualization_library", length = 100)
    private String visualizationLibrary;

    @Size(max = 30)
    @Column(name = "library_version", length = 30)
    private String libraryVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "generation_status", nullable = false, length = 20)
    @Builder.Default
    private ArtifactStatus generationStatus = ArtifactStatus.PENDING;

    @Size(max = 64)
    @Column(name = "content_hash", length = 64)
    private String contentHash;

    @Size(max = 50)
    @Column(name = "prompt_version", length = 50)
    private String promptVersion;

    @Size(max = 100)
    @Column(name = "model_name", length = 100)
    private String modelName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ── Relationships ──────────────────────────────────────────────────

    /**
     * Users who studied this artifact. Does NOT cascade delete learning history
     * if an artifact is modified or purged.
     */
    @OneToMany(mappedBy = "algorithmArtifact")
    @Builder.Default
    private List<LearningHistory> learningHistories = new ArrayList<>();
}
