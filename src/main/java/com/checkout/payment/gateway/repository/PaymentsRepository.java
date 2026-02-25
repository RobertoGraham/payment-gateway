package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.mapper.PostPaymentResponseMapper;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import com.checkout.payment.gateway.model.ProcessPaymentResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Repository
public class PaymentsRepository {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentsRepository.class);

  private final RestTemplate restTemplate;
  private final PostPaymentResponseMapper postPaymentResponseMapper = PostPaymentResponseMapper.INSTANCE;

  public PaymentsRepository (RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  private final HashMap<UUID, PostPaymentResponse> payments = new HashMap<>();

  public void add(PostPaymentResponse payment) {
    payments.put(payment.getId(), payment);
  }

  public Optional<PostPaymentResponse> get(UUID id) {
    return Optional.ofNullable(payments.get(id));
  }

  public PostPaymentResponse processPayment(@Valid PostPaymentRequest paymentRequest) throws Exception {
    ProcessPaymentResponse processPaymentResponse = null;
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
      headers.set("User-Agent", "Back-end tech test");
      HttpEntity<PostPaymentRequest> requestEntity = new HttpEntity<>(paymentRequest, headers);
      processPaymentResponse = restTemplate.exchange(
        "http://localhost:8080/payments",
        HttpMethod.POST,
        requestEntity,
        ProcessPaymentResponse.class)
        .getBody();
      } catch (RestClientException e) {
        LOG.error(e.getMessage(), e);
        System.out.println(e);
      }
      PostPaymentResponse postPaymentResponse = null;

      if (processPaymentResponse != null) {
        postPaymentResponse = errorHandleProductMapping(processPaymentResponse, paymentRequest);
      }

      if (postPaymentResponse != null && postPaymentResponse.getStatus().equals(PaymentStatus.AUTHORIZED)) {
        add(postPaymentResponse);
      }

      return postPaymentResponse;
    }

    private PostPaymentResponse errorHandleProductMapping(
        ProcessPaymentResponse processPaymentResponse, PostPaymentRequest paymentRequest) {
      try {
        return postPaymentResponseMapper.toPostPaymentResponse(paymentRequest, processPaymentResponse);
      } catch (final Exception e) {
        LOG.error(e.getMessage(), e);
        System.out.println(e);
        return null;
      }
    }
}


