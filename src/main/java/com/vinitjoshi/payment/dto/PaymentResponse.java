package com.vinitjoshi.payment.dto;

import com.vinitjoshi.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        String transactionReference,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String customerId,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}