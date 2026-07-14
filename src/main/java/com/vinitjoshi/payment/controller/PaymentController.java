package com.vinitjoshi.payment.controller;

import com.vinitjoshi.payment.dto.CreatePaymentRequest;
import com.vinitjoshi.payment.dto.DeletePaymentResponse;
import com.vinitjoshi.payment.dto.PaymentResponse;
import com.vinitjoshi.payment.dto.UpdatePaymentStatusRequest;
import com.vinitjoshi.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {

        return ResponseEntity.ok(
                paymentService.updatePaymentStatus(id, request)
        );
    }

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