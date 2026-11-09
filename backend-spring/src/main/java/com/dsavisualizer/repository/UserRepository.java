package com.dsavisualizer.repository;

import com.dsavisualizer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user using their email address
    Optional<User> findByEmail(String email);

    // Check whether an email is already registered
    boolean existsByEmail(String email);

    // Find all active users
    List<User> findByIsActiveTrue();

}