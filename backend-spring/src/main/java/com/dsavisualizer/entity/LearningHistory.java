package com.dsavisualizer.entity;

import com.dsavisualizer.enums.LearningStatus;
import com.dsavisualizer.enums.RequestType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "learning_histories", indexes = {
        @Index(name = "idx_lh_user", columnList = "user_id"),
        @Index(name = "idx_lh_artifact", columnList = "artifact_id"),
        @Index(name = "idx_lh_status", columnList = "status"),
        @Index(name = "idx_lh_created", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artifact_id")
    private AlgorithmArtifact algorithmArtifact;

    @Column(columnDefinition = "TEXT")
    private String originalQuery;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestType requestType;

    /**
     * Optional example input the user supplied during the session.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> inputData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LearningStatus status = LearningStatus.STARTED;

    @Min(value = 0, message = "Time spent cannot be negative")
    private Integer timeSpentSeconds;

    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
