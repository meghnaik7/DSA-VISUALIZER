package com.dsavisualizer.dto.subscription;

import com.dsavisualizer.enums.BillingInterval;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record SubscriptionPlanCreateRequest(

        @NotBlank(message = "Plan name is required")
        @Size(max = 100)
        String name,

        @NotBlank(message = "Plan code is required")
        @Size(max = 50)
        @Pattern(regexp = "^[A-Z0-9_-]+$")
        String code,

        @Size(max = 2000)
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00")
        BigDecimal price,

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "^[A-Z]{3}$",
                message = "Currency must be a three-letter uppercase code")
        String currency,

        @NotNull(message = "Billing interval is required")
        BillingInterval billingInterval,

        @NotNull(message = "AI request limit is required")
        @PositiveOrZero
        Integer maxAiRequests

) {}
