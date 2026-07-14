package com.vinitjoshi.payment.controller;

import com.vinitjoshi.payment.dto.CreatePaymentRequest;
import com.vinitjoshi.payment.dto.DeletePaymentResponse;
import com.vinitjoshi.payment.dto.PaymentResponse;
import com.vinitjoshi.payment.dto.UpdatePaymentStatusRequest;
import com.vinitjoshi.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@Tag(
        name = "Payments",
        description = "Payment creation, retrieval, status update and deletion APIs"
)
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(
            summary = "Create a payment",
            description = "Creates a new payment with PENDING status"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Payment created successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid payment request"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Transaction reference already exists"
    )
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        PaymentResponse response = paymentService.createPayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get payment by ID",
            description = "Retrieves a payment using its unique UUID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Payment retrieved successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Payment not found"
    )
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                paymentService.getPayment(id)
        );
    }

    @Operation(
            summary = "Get all payments",
            description = "Retrieves all payments currently stored in the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Payments retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {

        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );
    }

    @Operation(
            summary = "Update payment status",
            description = "Updates the status of an existing payment"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Payment status updated successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid payment status"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Payment not found"
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {

        return ResponseEntity.ok(
                paymentService.updatePaymentStatus(id, request)
        );
    }

    @Operation(
            summary = "Delete payment",
            description = "Deletes an existing payment using its unique UUID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Payment deleted successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Payment not found"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletePaymentResponse> deletePayment(
            @PathVariable UUID id) {

        paymentService.deletePayment(id);

        DeletePaymentResponse response = new DeletePaymentResponse(
                id,
                "Payment deleted successfully",
                Instant.now()
        );

        return ResponseEntity.ok(response);
    }
}