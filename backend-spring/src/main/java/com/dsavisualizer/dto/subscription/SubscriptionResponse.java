package com.dsavisualizer.dto.subscription;

import com.dsavisualizer.enums.SubscriptionStatus;
import java.time.LocalDateTime;

public record SubscriptionResponse(

        Long id,
        Long planId,
        String planName,
        SubscriptionStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Boolean autoRenew,
        LocalDateTime createdAt

) {}
