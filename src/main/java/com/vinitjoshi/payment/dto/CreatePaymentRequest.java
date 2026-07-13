package com.vinitjoshi.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreatePaymentRequest(

        @NotBlank
        @Size(max = 100)
        String transactionReference,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotBlank
        @Pattern(regexp = "^[A-Za-z]{3}$", message = "Currency must be a 3-letter code")
        String currency,

        @NotBlank
        @Size(max = 100)
        String customerId,

        @Size(max = 255)
        String description
) {
}