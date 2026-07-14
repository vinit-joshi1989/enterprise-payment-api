package com.vinitjoshi.payment.dto;

import java.time.Instant;
import java.util.UUID;

public record DeletePaymentResponse(
        UUID id,
        String message,
        Instant timestamp
) {
}