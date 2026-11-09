package com.dsavisualizer.dto.subscription;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SubscriptionCheckoutRequest(

        @NotNull(message = "Plan ID is required")
        @Positive(message = "Plan ID must be positive")
        Long planId

) {}
