package com.vinitjoshi.payment.dto;

import com.vinitjoshi.payment.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentStatusRequest(

        @NotNull(message = "Payment status is required")
        PaymentStatus status

) {
}