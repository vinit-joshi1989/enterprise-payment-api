package com.vinitjoshi.payment.exception;

public class DuplicatePaymentException extends RuntimeException {

    public DuplicatePaymentException(String transactionReference) {
        super("Payment already exists with transaction reference: " + transactionReference);
    }
}