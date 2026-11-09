package com.dsavisualizer.dto.payment;

import com.dsavisualizer.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(

        Long id,
        Long subscriptionId,
        BigDecimal amount,
        String currency,
        String provider,
        PaymentStatus status,
        String paymentMethod,
        LocalDateTime paidAt,
        LocalDateTime createdAt

) {}
