package com.dsavisualizer.repository;

import com.dsavisualizer.entity.Payment;
import com.dsavisualizer.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find a payment using the payment provider's transaction ID
    Optional<Payment> findByProviderPaymentId(String providerPaymentId);

    // Find all payments belonging to a user
    List<Payment> findByUser_Id(Long userId);

    // Find payments belonging to a user with a particular status
    List<Payment> findByUser_IdAndStatus(
            Long userId,
            PaymentStatus status
    );

    // Find all payments having a particular status
    List<Payment> findByStatus(PaymentStatus status);

    // Check whether a payment provider transaction ID already exists
    boolean existsByProviderPaymentId(String providerPaymentId);

}