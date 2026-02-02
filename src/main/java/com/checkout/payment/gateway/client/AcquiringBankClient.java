package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.exception.AcquiringBankClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.net.URI;

@Component
public class AcquiringBankClient {

  private static final Logger LOG = LoggerFactory.getLogger(AcquiringBankClient.class);

  private final RestTemplate restTemplate;
  private final String acquiringBankUrl;

  public AcquiringBankClient(
      RestTemplate restTemplate,
      @Value("${acquiring.bank.url}")
      String acquiringBankUrl) {
    this.restTemplate = restTemplate;
    this.acquiringBankUrl = acquiringBankUrl;
  }

  public AcquiringBankPaymentResponse makePaymentRequest(
      AcquiringBankPaymentRequest acquiringBankPaymentRequest) throws AcquiringBankClientException {
    String cardNumberLastFour = acquiringBankPaymentRequest.cardNumber().substring(
        acquiringBankPaymentRequest.cardNumber().length() - 4);
    LOG.debug("Making payment request to acquiring bank for card number last four: {}", cardNumberLastFour);
    try {
      ResponseEntity<AcquiringBankPaymentResponse> response = restTemplate.postForEntity(
          URI.create(String.format("%s/payments", acquiringBankUrl)),
          acquiringBankPaymentRequest,
          AcquiringBankPaymentResponse.class
      );
      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new AcquiringBankClientException(String.format("Create payment failed with http status: %d", response.getStatusCode().value()));
      } else {
        LOG.debug("Received successful response from acquiring bank for card number last four: {}", cardNumberLastFour);
        return response.getBody();
      }
    } catch (RestClientException e) {
      throw new AcquiringBankClientException("An error occurred contacting the acquiring bank");
    }
  }
}
