package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;

public interface AcquiringBankClient {

  BankPaymentResponse requestAuthorization(BankPaymentRequest request);
}
