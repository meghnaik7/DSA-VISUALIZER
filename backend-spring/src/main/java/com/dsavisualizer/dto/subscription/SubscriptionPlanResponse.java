package com.dsavisualizer.dto.subscription;

import com.dsavisualizer.enums.BillingInterval;
import java.math.BigDecimal;

public record SubscriptionPlanResponse(

        Long id,
        String name,
        String code,
        String description,
        BigDecimal price,
        String currency,
        BillingInterval billingInterval,
        Integer maxAiRequests,
        Boolean isActive

) {}
