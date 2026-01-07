package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AcquiringBankRestClient implements AcquiringBankClient {

  private static final String PAYMENTS_PATH = "/payments";

  private final RestTemplate restTemplate;
  private final String bankBaseUrl;

  public AcquiringBankRestClient(RestTemplate restTemplate,
      @Value("${bank.baseUrl}") String bankBaseUrl) {
    this.restTemplate = restTemplate;
    this.bankBaseUrl = bankBaseUrl;
  }

  @Override
  public BankPaymentResponse requestAuthorization(BankPaymentRequest request) {
    return restTemplate.postForObject(bankBaseUrl + PAYMENTS_PATH, request,
        BankPaymentResponse.class);
  }
}

