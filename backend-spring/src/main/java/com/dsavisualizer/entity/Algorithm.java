package com.dsavisualizer.entity;

import com.dsavisualizer.enums.AlgorithmCategory;
import com.dsavisualizer.enums.Difficulty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "algorithms", indexes = {
        @Index(name = "idx_algorithm_cat_diff", columnList = "category, difficulty")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Algorithm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Algorithm name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(max = 200, message = "Slug must not exceed 200 characters")
    @Column(nullable = false, unique = true, length = 200)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AlgorithmCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 100)
    @Column(name = "input_type", length = 100)
    private String inputType;

    @Size(max = 100)
    @Column(name = "output_type", length = 100)
    private String outputType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ── Relationships ──────────────────────────────────────────────────

    /**
     * Bidirectional many-to-many: problems solved by this algorithm.
     */
    @ManyToMany(mappedBy = "algorithms")
    @Builder.Default
    private Set<Problem> problems = new HashSet<>();

    @OneToMany(mappedBy = "algorithm", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AlgorithmArtifact> artifacts = new ArrayList<>();
}
