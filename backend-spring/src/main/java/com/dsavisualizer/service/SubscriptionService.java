package com.dsavisualizer.service;

import com.dsavisualizer.dto.subscription.SubscriptionCheckoutRequest;
import com.dsavisualizer.dto.subscription.SubscriptionResponse;

import com.dsavisualizer.entity.Subscription;
import com.dsavisualizer.entity.SubscriptionPlan;
import com.dsavisualizer.entity.User;

import com.dsavisualizer.enums.SubscriptionStatus;

import com.dsavisualizer.repository.SubscriptionRepository;
import com.dsavisualizer.repository.SubscriptionPlanRepository;
import com.dsavisualizer.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserRepository userRepository;

    // Create a pending subscription for checkout
    @Transactional
    public SubscriptionResponse createSubscription(
            Long userId,
            SubscriptionCheckoutRequest request) {

        // 1. Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2. Check whether user is active
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new IllegalArgumentException(
                    "Inactive users cannot create subscriptions");
        }

        // 3. Find subscription plan
        SubscriptionPlan plan = subscriptionPlanRepository.findById(request.planId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subscription plan not found"));

        // 4. Check whether plan is active
        if (!Boolean.TRUE.equals(plan.getIsActive())) {
            throw new IllegalArgumentException(
                    "This subscription plan is not available");
        }

        // 5. Create subscription
        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .status(SubscriptionStatus.PENDING)
                .startDate(LocalDateTime.now())
                .autoRenew(false)
                .build();

        // 6. Save subscription
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // 7. Return response
        return mapToResponse(savedSubscription);
    }

    // Get all subscriptions belonging to a user
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getUserSubscriptions(Long userId) {

        return subscriptionRepository.findByUser_Id(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get subscriptions belonging to a user with a particular status
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getUserSubscriptionsByStatus(
            Long userId,
            SubscriptionStatus status) {

        return subscriptionRepository
                .findByUser_IdAndStatus(userId, status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get subscription by ID
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionById(Long subscriptionId) {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subscription not found"));

        return mapToResponse(subscription);
    }

    // Convert entity into response DTO
    private SubscriptionResponse mapToResponse(
            Subscription subscription) {

        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getPlan().getId(),
                subscription.getPlan().getName(),
                subscription.getStatus(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getAutoRenew(),
                subscription.getCreatedAt());
    }
}