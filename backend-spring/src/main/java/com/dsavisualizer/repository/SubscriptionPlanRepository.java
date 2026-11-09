package com.dsavisualizer.repository;

import com.dsavisualizer.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository
        extends JpaRepository<SubscriptionPlan, Long> {

    // Find a subscription plan using its unique code
    Optional<SubscriptionPlan> findByCode(String code);

    // Check whether a plan code already exists
    boolean existsByCode(String code);

    // Find all active subscription plans
    List<SubscriptionPlan> findByIsActiveTrue();

}