package com.vinitjoshi.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinitjoshi.payment.dto.CreatePaymentRequest;
import com.vinitjoshi.payment.dto.PaymentResponse;
import com.vinitjoshi.payment.dto.UpdatePaymentStatusRequest;
import com.vinitjoshi.payment.entity.PaymentStatus;
import com.vinitjoshi.payment.exception.DuplicatePaymentException;
import com.vinitjoshi.payment.exception.GlobalExceptionHandler;
import com.vinitjoshi.payment.exception.PaymentNotFoundException;
import com.vinitjoshi.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    private final UUID paymentId =
            UUID.fromString("3b9e3e41-781f-42d7-8409-3e7f62f2f786");

    private final Instant createdAt =
            Instant.parse("2026-07-14T10:00:00Z");

    private final Instant updatedAt =
            Instant.parse("2026-07-14T10:05:00Z");

    @Test
    void createPayment_shouldReturn201Created() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest(
                "TXN-TEST-001",
                new BigDecimal("125.50"),
                "EUR",
                "CUST-001",
                "Test payment"
        );

        PaymentResponse response = createPaymentResponse(
                PaymentStatus.PENDING
        );

        when(paymentService.createPayment(any(CreatePaymentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(paymentId.toString()))
                .andExpect(jsonPath("$.transactionReference")
                        .value("TXN-TEST-001"))
                .andExpect(jsonPath("$.amount").value(125.50))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.customerId").value("CUST-001"))
                .andExpect(jsonPath("$.description")
                        .value("Test payment"));

        verify(paymentService)
                .createPayment(any(CreatePaymentRequest.class));
    }

    @Test
    void createPayment_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

        String invalidRequest = """
                {
                  "transactionReference": "",
                  "amount": 0,
                  "currency": "EU",
                  "customerId": "",
                  "description": "Invalid payment"
                }
                """;

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.transactionReference")
                        .exists())
                .andExpect(jsonPath("$.fieldErrors.amount")
                        .exists())
                .andExpect(jsonPath("$.fieldErrors.currency")
                        .exists())
                .andExpect(jsonPath("$.fieldErrors.customerId")
                        .exists());
    }

    @Test
    void createPayment_shouldReturn409_whenReferenceAlreadyExists()
            throws Exception {

        CreatePaymentRequest request = new CreatePaymentRequest(
                "TXN-TEST-001",
                new BigDecimal("125.50"),
                "EUR",
                "CUST-001",
                "Test payment"
        );

        when(paymentService.createPayment(
                any(CreatePaymentRequest.class)))
                .thenThrow(
                        new DuplicatePaymentException("TXN-TEST-001")
                );

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Payment already exists with transaction reference: "
                                        + "TXN-TEST-001"
                        ));
    }

    @Test
    void getPayment_shouldReturn200_whenPaymentExists()
            throws Exception {

        when(paymentService.getPayment(paymentId))
                .thenReturn(
                        createPaymentResponse(PaymentStatus.PENDING)
                );

        mockMvc.perform(get("/api/payments/{id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(paymentId.toString()))
                .andExpect(jsonPath("$.transactionReference")
                        .value("TXN-TEST-001"))
                .andExpect(jsonPath("$.status")
                        .value("PENDING"));

        verify(paymentService).getPayment(paymentId);
    }

    @Test
    void getPayment_shouldReturn404_whenPaymentDoesNotExist()
            throws Exception {

        when(paymentService.getPayment(paymentId))
                .thenThrow(new PaymentNotFoundException(paymentId));

        mockMvc.perform(get("/api/payments/{id}", paymentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Payment not found with id: " + paymentId));
    }

    @Test
    void getAllPayments_shouldReturn200AndPaymentList()
            throws Exception {

        when(paymentService.getAllPayments())
                .thenReturn(
                        List.of(
                                createPaymentResponse(
                                        PaymentStatus.PENDING
                                )
                        )
                );

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(paymentId.toString()))
                .andExpect(jsonPath("$[0].status")
                        .value("PENDING"));

        verify(paymentService).getAllPayments();
    }

    @Test
    void updatePaymentStatus_shouldReturn200()
            throws Exception {

        UpdatePaymentStatusRequest request =
                new UpdatePaymentStatusRequest(
                        PaymentStatus.COMPLETED
                );

        when(paymentService.updatePaymentStatus(
                eq(paymentId),
                any(UpdatePaymentStatusRequest.class)))
                .thenReturn(
                        createPaymentResponse(
                                PaymentStatus.COMPLETED
                        )
                );

        mockMvc.perform(patch(
                        "/api/payments/{id}/status",
                        paymentId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(paymentId.toString()))
                .andExpect(jsonPath("$.status")
                        .value("COMPLETED"))
                .andExpect(jsonPath("$.updatedAt")
                        .value(updatedAt.toString()));

        verify(paymentService).updatePaymentStatus(
                eq(paymentId),
                any(UpdatePaymentStatusRequest.class)
        );
    }

    @Test
    void updatePaymentStatus_shouldReturn400_whenStatusIsMissing()
            throws Exception {

        String invalidRequest = """
                {
                  "status": null
                }
                """;

        mockMvc.perform(patch(
                        "/api/payments/{id}/status",
                        paymentId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.status")
                        .value("Payment status is required"));
    }

    @Test
    void deletePayment_shouldReturn200WithConfirmation()
            throws Exception {

        doNothing()
                .when(paymentService)
                .deletePayment(paymentId);

        mockMvc.perform(delete("/api/payments/{id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(paymentId.toString()))
                .andExpect(jsonPath("$.message")
                        .value("Payment deleted successfully"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(paymentService).deletePayment(paymentId);
    }

    @Test
    void deletePayment_shouldReturn404_whenPaymentDoesNotExist()
            throws Exception {

        doThrow(new PaymentNotFoundException(paymentId))
                .when(paymentService)
                .deletePayment(paymentId);

        mockMvc.perform(delete("/api/payments/{id}", paymentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Payment not found with id: " + paymentId));
    }

    private PaymentResponse createPaymentResponse(
            PaymentStatus status) {

        return new PaymentResponse(
                paymentId,
                "TXN-TEST-001",
                new BigDecimal("125.50"),
                "EUR",
                status,
                "CUST-001",
                "Test payment",
                createdAt,
                updatedAt
        );
    }
}