package com.dsavisualizer.repository;

import com.dsavisualizer.entity.Subscription;
import com.dsavisualizer.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    // Find all subscriptions belonging to a user
    List<Subscription> findByUser_Id(Long userId);

    // Find subscriptions belonging to a user with a particular status
    List<Subscription> findByUser_IdAndStatus(
            Long userId,
            SubscriptionStatus status
    );

    // Find a subscription using its payment provider subscription ID
    Optional<Subscription> findByProviderSubscriptionId(
            String providerSubscriptionId
    );

    // Find all subscriptions having a particular status
    List<Subscription> findByStatus(SubscriptionStatus status);

}