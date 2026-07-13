package com.vinitjoshi.payment.repository;

import com.vinitjoshi.payment.entity.Payment;
import com.vinitjoshi.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByTransactionReference(String transactionReference);

    boolean existsByTransactionReference(String transactionReference);

    List<Payment> findByStatus(PaymentStatus status);
}