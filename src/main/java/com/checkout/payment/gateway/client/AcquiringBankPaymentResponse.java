package com.checkout.payment.gateway.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AcquiringBankPaymentResponse(
    boolean authorized,
    @JsonProperty("authorization_code")
    UUID authorizationCode
) {

}
