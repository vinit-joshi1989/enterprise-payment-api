package com.vinitjoshi.payment.service;

import com.vinitjoshi.payment.dto.CreatePaymentRequest;
import com.vinitjoshi.payment.dto.PaymentResponse;
import com.vinitjoshi.payment.entity.Payment;
import com.vinitjoshi.payment.entity.PaymentStatus;
import com.vinitjoshi.payment.exception.DuplicatePaymentException;
import com.vinitjoshi.payment.exception.PaymentNotFoundException;
import com.vinitjoshi.payment.repository.PaymentRepository;
import com.vinitjoshi.payment.dto.UpdatePaymentStatusRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponse createPayment(CreatePaymentRequest request) {
        if (paymentRepository.existsByTransactionReference(request.transactionReference())) {
            throw new DuplicatePaymentException(request.transactionReference());
        }

        Payment payment = Payment.builder()
                .transactionReference(request.transactionReference())
                .amount(request.amount())
                .currency(request.currency())
                .customerId(request.customerId())
                .description(request.description())
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return toResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getTransactionReference(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCustomerId(),
                payment.getDescription(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    public PaymentResponse updatePaymentStatus(
            UUID id,
            UpdatePaymentStatusRequest request) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        payment.setStatus(request.status());

        Payment updatedPayment = paymentRepository.saveAndFlush(payment);

        return toResponse(updatedPayment);
    }

    public void deletePayment(UUID id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        paymentRepository.delete(payment);
    }
}