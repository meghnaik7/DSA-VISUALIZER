package com.dsavisualizer.controller;

import com.dsavisualizer.dto.payment.PaymentCreateRequest;
import com.dsavisualizer.dto.payment.PaymentResponse;
import com.dsavisualizer.enums.PaymentStatus;
import com.dsavisualizer.service.PaymentService;
import com.dsavisualizer.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    // Create a pending payment
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentCreateRequest request,
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        PaymentResponse response = paymentService.createPendingPayment(
                userId,
                request.subscriptionId(),
                request.provider());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all payments of logged-in user
    @GetMapping("/my")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        List<PaymentResponse> payments = paymentService.getUserPayments(userId);

        return ResponseEntity.ok(payments);
    }

    // Get payments by status
    @GetMapping("/my/status/{status}")
    public ResponseEntity<List<PaymentResponse>> getMyPaymentsByStatus(
            @PathVariable PaymentStatus status,
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        List<PaymentResponse> payments = paymentService.getUserPaymentsByStatus(
                userId,
                status);

        return ResponseEntity.ok(payments);
    }

    // Get authenticated user's database ID
    private Long getAuthenticatedUserId(Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("User is not authenticated");
        }

        return userService.getUserIdByEmail(authentication.getName());
    }
}