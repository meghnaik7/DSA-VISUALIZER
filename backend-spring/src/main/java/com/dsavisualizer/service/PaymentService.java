package com.dsavisualizer.service;

import com.dsavisualizer.dto.payment.PaymentResponse;
import com.dsavisualizer.entity.Payment;
import com.dsavisualizer.entity.Subscription;
import com.dsavisualizer.enums.BillingInterval;
import com.dsavisualizer.enums.PaymentStatus;
import com.dsavisualizer.enums.SubscriptionStatus;
import com.dsavisualizer.repository.PaymentRepository;
import com.dsavisualizer.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;

    // Create a pending payment for a subscription
    @Transactional
    public PaymentResponse createPendingPayment(
            Long userId,
            Long subscriptionId,
            String provider) {

        // 1. Find subscription
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subscription not found"));

        // 2. Verify subscription ownership
        if (!subscription.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    "You cannot make payment for this subscription");
        }

        // 3. Check subscription status
        if (subscription.getStatus() != SubscriptionStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Payment can only be created for pending subscriptions");
        }

        // 4. Create payment record
        Payment payment = Payment.builder()
                .user(subscription.getUser())
                .subscription(subscription)
                .amount(subscription.getPlan().getPrice())
                .currency(subscription.getPlan().getCurrency())
                .provider(provider)
                .status(PaymentStatus.PENDING)
                .build();

        // 5. Save payment
        Payment savedPayment = paymentRepository.save(payment);

        // 6. Return response
        return mapToResponse(savedPayment);
    }

    // Attach payment gateway transaction ID
    @Transactional
    public PaymentResponse attachProviderPaymentId(
            Long paymentId,
            String providerPaymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending payments can be updated");
        }

        if (providerPaymentId == null ||
                providerPaymentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Provider payment ID is required");
        }

        String normalizedId = providerPaymentId.trim();

        if (paymentRepository.existsByProviderPaymentId(normalizedId)) {
            throw new IllegalArgumentException(
                    "Payment provider transaction ID already exists");
        }

        payment.setProviderPaymentId(normalizedId);

        return mapToResponse(paymentRepository.save(payment));
    }

    // Mark payment successful after verifying gateway confirmation
    @Transactional
    public PaymentResponse markPaymentSuccessful(
            String providerPaymentId,
            String paymentMethod) {

        Payment payment = paymentRepository
                .findByProviderPaymentId(providerPaymentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Payment is not in pending status");
        }

        Subscription subscription = payment.getSubscription();

        if (subscription == null ||
                subscription.getStatus() != SubscriptionStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Subscription cannot be activated");
        }

        // Update payment
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaidAt(LocalDateTime.now());

        // Activate subscription
        LocalDateTime now = LocalDateTime.now();

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(now);
        subscription.setEndDate(
                calculateEndDate(now, subscription.getPlan().getBillingInterval()));

        paymentRepository.save(payment);
        subscriptionRepository.save(subscription);

        return mapToResponse(payment);
    }

    // Mark payment as failed
    @Transactional
    public PaymentResponse markPaymentFailed(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending payments can be marked as failed");
        }

        payment.setStatus(PaymentStatus.FAILED);

        return mapToResponse(paymentRepository.save(payment));
    }

    // Get all payments belonging to a user
    @Transactional(readOnly = true)
    public List<PaymentResponse> getUserPayments(Long userId) {

        return paymentRepository.findByUser_Id(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get payments belonging to a user with a particular status
    @Transactional(readOnly = true)
    public List<PaymentResponse> getUserPaymentsByStatus(
            Long userId,
            PaymentStatus status) {

        return paymentRepository.findByUser_IdAndStatus(userId, status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Calculate subscription end date
    private LocalDateTime calculateEndDate(
            LocalDateTime startDate,
            BillingInterval billingInterval) {

        return switch (billingInterval) {
            case MONTHLY -> startDate.plusMonths(1);
            case YEARLY -> startDate.plusYears(1);
            case LIFETIME -> null;
        };
    }

    // Convert entity into response DTO
    private PaymentResponse mapToResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getSubscription() != null
                        ? payment.getSubscription().getId()
                        : null,
                payment.getAmount(),
                payment.getCurrency(),
                payment.getProvider(),
                payment.getStatus(),
                payment.getPaymentMethod(),
                payment.getPaidAt(),
                payment.getCreatedAt());
    }
}