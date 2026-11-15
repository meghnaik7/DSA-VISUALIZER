package com.dsavisualizer.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentCreateRequest(

        @NotNull(message = "Subscription ID is required") @Positive(message = "Subscription ID must be positive") Long subscriptionId,

        @NotBlank(message = "Payment provider is required") String provider

) {
}