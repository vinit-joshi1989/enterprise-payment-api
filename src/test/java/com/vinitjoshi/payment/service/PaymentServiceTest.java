package com.vinitjoshi.payment.service;

import com.vinitjoshi.payment.dto.CreatePaymentRequest;
import com.vinitjoshi.payment.dto.PaymentResponse;
import com.vinitjoshi.payment.dto.UpdatePaymentStatusRequest;
import com.vinitjoshi.payment.entity.Payment;
import com.vinitjoshi.payment.entity.PaymentStatus;
import com.vinitjoshi.payment.exception.DuplicatePaymentException;
import com.vinitjoshi.payment.exception.PaymentNotFoundException;
import com.vinitjoshi.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private UUID paymentId;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();

        payment = Payment.builder()
                .id(paymentId)
                .transactionReference("TXN-TEST-001")
                .amount(new BigDecimal("125.50"))
                .currency("EUR")
                .status(PaymentStatus.PENDING)
                .customerId("CUST-001")
                .description("Test payment")
                .createdAt(Instant.parse("2026-07-14T10:00:00Z"))
                .updatedAt(Instant.parse("2026-07-14T10:00:00Z"))
                .build();
    }

    @Test
    void createPayment_shouldCreatePayment_whenReferenceIsUnique() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                "TXN-TEST-001",
                new BigDecimal("125.50"),
                "EUR",
                "CUST-001",
                "Test payment"
        );

        when(paymentRepository.existsByTransactionReference("TXN-TEST-001"))
                .thenReturn(false);

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(payment);

        PaymentResponse response = paymentService.createPayment(request);

        assertNotNull(response);
        assertEquals(paymentId, response.id());
        assertEquals("TXN-TEST-001", response.transactionReference());
        assertEquals(new BigDecimal("125.50"), response.amount());
        assertEquals("EUR", response.currency());
        assertEquals(PaymentStatus.PENDING, response.status());

        verify(paymentRepository).existsByTransactionReference("TXN-TEST-001");
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void createPayment_shouldThrowDuplicatePaymentException_whenReferenceExists() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                "TXN-TEST-001",
                new BigDecimal("125.50"),
                "EUR",
                "CUST-001",
                "Test payment"
        );

        when(paymentRepository.existsByTransactionReference("TXN-TEST-001"))
                .thenReturn(true);

        assertThrows(
                DuplicatePaymentException.class,
                () -> paymentService.createPayment(request)
        );

        verify(paymentRepository).existsByTransactionReference("TXN-TEST-001");
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void getPayment_shouldReturnPayment_whenPaymentExists() {
        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPayment(paymentId);

        assertNotNull(response);
        assertEquals(paymentId, response.id());
        assertEquals("TXN-TEST-001", response.transactionReference());

        verify(paymentRepository).findById(paymentId);
    }

    @Test
    void getPayment_shouldThrowPaymentNotFoundException_whenPaymentDoesNotExist() {
        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.getPayment(paymentId)
        );

        verify(paymentRepository).findById(paymentId);
    }

    @Test
    void getAllPayments_shouldReturnAllPayments() {
        when(paymentRepository.findAll())
                .thenReturn(List.of(payment));

        List<PaymentResponse> responses = paymentService.getAllPayments();

        assertEquals(1, responses.size());
        assertEquals(paymentId, responses.get(0).id());

        verify(paymentRepository).findAll();
    }

    @Test
    void updatePaymentStatus_shouldUpdateAndReturnPayment() {
        UpdatePaymentStatusRequest request =
                new UpdatePaymentStatusRequest(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.saveAndFlush(payment))
                .thenReturn(payment);

        PaymentResponse response =
                paymentService.updatePaymentStatus(paymentId, request);

        assertEquals(PaymentStatus.COMPLETED, response.status());
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());

        verify(paymentRepository).findById(paymentId);
        verify(paymentRepository).saveAndFlush(payment);
    }

    @Test
    void updatePaymentStatus_shouldThrowPaymentNotFoundException_whenPaymentDoesNotExist() {
        UpdatePaymentStatusRequest request =
                new UpdatePaymentStatusRequest(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.updatePaymentStatus(paymentId, request)
        );

        verify(paymentRepository).findById(paymentId);
        verify(paymentRepository, never()).saveAndFlush(any(Payment.class));
    }

    @Test
    void deletePayment_shouldDeletePayment_whenPaymentExists() {
        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        paymentService.deletePayment(paymentId);

        verify(paymentRepository).findById(paymentId);
        verify(paymentRepository).delete(payment);
    }

    @Test
    void deletePayment_shouldThrowPaymentNotFoundException_whenPaymentDoesNotExist() {
        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.deletePayment(paymentId)
        );

        verify(paymentRepository).findById(paymentId);
        verify(paymentRepository, never()).delete(any(Payment.class));
    }
}