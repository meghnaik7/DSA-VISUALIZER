package com.dsavisualizer.service;

import com.dsavisualizer.dto.subscription.SubscriptionPlanCreateRequest;
import com.dsavisualizer.dto.subscription.SubscriptionPlanResponse;
import com.dsavisualizer.entity.SubscriptionPlan;
import com.dsavisualizer.repository.SubscriptionPlanRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    // Create a new subscription plan
    @Transactional
    public SubscriptionPlanResponse createPlan(
            SubscriptionPlanCreateRequest request) {

        // 1. Normalize plan code
        String code = request.code()
                .trim()
                .toUpperCase(Locale.ROOT);

        // 2. Check whether plan code already exists
        if (subscriptionPlanRepository.existsByCode(code)) {
            throw new IllegalArgumentException(
                    "Subscription plan code already exists");
        }

        // 3. Create subscription plan entity
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .name(request.name().trim())
                .code(code)
                .description(request.description())
                .price(request.price())
                .currency(request.currency().trim().toUpperCase(Locale.ROOT))
                .billingInterval(request.billingInterval())
                .maxAiRequests(request.maxAiRequests())
                .isActive(true)
                .build();

        // 4. Save plan
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);

        // 5. Convert entity into response DTO
        return mapToResponse(savedPlan);
    }

    // Get all active subscription plans
    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponse> getActivePlans() {

        return subscriptionPlanRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get subscription plan by ID
    @Transactional(readOnly = true)
    public SubscriptionPlanResponse getPlanById(Long id) {

        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subscription plan not found"));

        return mapToResponse(plan);
    }

    // Get subscription plan by code
    @Transactional(readOnly = true)
    public SubscriptionPlanResponse getPlanByCode(String code) {

        String normalizedCode = code.trim()
                .toUpperCase(Locale.ROOT);

        SubscriptionPlan plan = subscriptionPlanRepository.findByCode(normalizedCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subscription plan not found"));

        return mapToResponse(plan);
    }

    // Convert entity into response DTO
    private SubscriptionPlanResponse mapToResponse(
            SubscriptionPlan plan) {

        return new SubscriptionPlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getCode(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getCurrency(),
                plan.getBillingInterval(),
                plan.getMaxAiRequests(),
                plan.getIsActive());
    }
}