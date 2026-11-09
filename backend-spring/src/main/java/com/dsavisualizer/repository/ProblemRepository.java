package com.dsavisualizer.repository;

import com.dsavisualizer.entity.Problem;
import com.dsavisualizer.enums.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    // Find a problem using its unique slug
    Optional<Problem> findBySlug(String slug);

    // Check whether a problem with the given slug exists
    boolean existsBySlug(String slug);

    // Find a problem using its canonical hash
    Optional<Problem> findByCanonicalHash(String canonicalHash);

    // Find all problems having a particular difficulty
    List<Problem> findByDifficulty(Difficulty difficulty);

    // Search problems by title (case-insensitive)
    List<Problem> findByTitleContainingIgnoreCase(String title);

}