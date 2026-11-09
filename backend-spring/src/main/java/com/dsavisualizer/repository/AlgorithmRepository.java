package com.dsavisualizer.repository;

import com.dsavisualizer.entity.Algorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlgorithmRepository extends JpaRepository<Algorithm, Long> {

    // Find an algorithm using its unique slug
    Optional<Algorithm> findBySlug(String slug);

    // Check whether an algorithm with the given slug exists
    boolean existsBySlug(String slug);

    // Find all active algorithms
    List<Algorithm> findByIsActiveTrue();

}