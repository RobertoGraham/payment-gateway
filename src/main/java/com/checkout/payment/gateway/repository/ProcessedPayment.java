package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.enums.PaymentStatus;
import java.util.UUID;

public record ProcessedPayment(
    UUID id,
    PaymentStatus paymentStatus,
    Integer cardNumberLastFour,
    Integer expiryMonth,
    Integer expiryYear,
    String currency,
    Integer amount
) {

}
