package com.dsavisualizer.controller;

import com.dsavisualizer.dto.subscription.SubscriptionPlanCreateRequest;
import com.dsavisualizer.dto.subscription.SubscriptionPlanResponse;
import com.dsavisualizer.service.SubscriptionPlanService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    // 1. Create a subscription plan
    @PostMapping
    public ResponseEntity<SubscriptionPlanResponse> createPlan(
            @Valid @RequestBody SubscriptionPlanCreateRequest request) {

        SubscriptionPlanResponse response = subscriptionPlanService.createPlan(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 2. Get all active subscription plans
    @GetMapping
    public ResponseEntity<List<SubscriptionPlanResponse>> getActivePlans() {

        List<SubscriptionPlanResponse> plans = subscriptionPlanService.getActivePlans();

        return ResponseEntity.ok(plans);
    }

    // 3. Get subscription plan by ID
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlanResponse> getPlanById(
            @PathVariable Long id) {

        SubscriptionPlanResponse response = subscriptionPlanService.getPlanById(id);

        return ResponseEntity.ok(response);
    }

    // 4. Get subscription plan by code
    @GetMapping("/code/{code}")
    public ResponseEntity<SubscriptionPlanResponse> getPlanByCode(
            @PathVariable String code) {

        SubscriptionPlanResponse response = subscriptionPlanService.getPlanByCode(code);

        return ResponseEntity.ok(response);
    }
}