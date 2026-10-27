package com.dsavisualizer.entity;

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
@Table(name = "problems", indexes = {
        @Index(name = "idx_problem_slug", columnList = "slug", unique = true),
        @Index(name = "idx_problem_canonical_hash", columnList = "canonicalHash"),
        @Index(name = "idx_problem_difficulty", columnList = "difficulty")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String title;

    @NotBlank(message = "Slug is required")
    @Size(max = 300, message = "Slug must not exceed 300 characters")
    @Column(nullable = false, unique = true, length = 300)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String statement;

    @Column(columnDefinition = "TEXT")
    private String inputDescription;

    @Column(columnDefinition = "TEXT")
    private String outputDescription;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @Size(max = 64, message = "Hash must not exceed 64 characters")
    @Column(length = 64)
    private String canonicalHash;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ── Relationships ──────────────────────────────────────────────────

    /**
     * Many-to-many: a problem can be solved by multiple algorithms,
     * and an algorithm can solve multiple problems.
     */
    @ManyToMany
    @JoinTable(
            name = "problem_algorithms",
            joinColumns = @JoinColumn(name = "problem_id"),
            inverseJoinColumns = @JoinColumn(name = "algorithm_id")
    )
    @Builder.Default
    private Set<Algorithm> algorithms = new HashSet<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AlgorithmArtifact> artifacts = new ArrayList<>();
}
