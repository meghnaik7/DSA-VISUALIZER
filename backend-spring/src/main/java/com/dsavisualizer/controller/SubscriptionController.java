package com.dsavisualizer.controller;

import com.dsavisualizer.dto.subscription.SubscriptionCheckoutRequest;
import com.dsavisualizer.dto.subscription.SubscriptionResponse;
import com.dsavisualizer.enums.SubscriptionStatus;
import com.dsavisualizer.service.SubscriptionService;
import com.dsavisualizer.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserService userService;

    // 1. Create a subscription
    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @Valid @RequestBody SubscriptionCheckoutRequest request,
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        SubscriptionResponse response = subscriptionService.createSubscription(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 2. Get all subscriptions of the logged-in user
    @GetMapping("/my")
    public ResponseEntity<List<SubscriptionResponse>> getMySubscriptions(
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        List<SubscriptionResponse> subscriptions = subscriptionService.getUserSubscriptions(userId);

        return ResponseEntity.ok(subscriptions);
    }

    // 3. Get subscriptions by status
    @GetMapping("/my/status/{status}")
    public ResponseEntity<List<SubscriptionResponse>> getMySubscriptionsByStatus(
            @PathVariable SubscriptionStatus status,
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        List<SubscriptionResponse> subscriptions = subscriptionService.getUserSubscriptionsByStatus(
                userId,
                status);

        return ResponseEntity.ok(subscriptions);
    }

    // Get the authenticated user's database ID
    private Long getAuthenticatedUserId(Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("User is not authenticated");
        }

        return userService.getUserIdByEmail(authentication.getName());
    }
}