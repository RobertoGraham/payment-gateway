package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.exception.AcquiringBankClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class AcquiringBankClientTest {

  private AcquiringBankClient underTest;

  @Mock
  private RestTemplate restTemplate;

  @Value("${acquiring.bank.url}")
  private String acquiringBankUrl;

  @BeforeEach
  void setUp() {
    underTest = new AcquiringBankClient(restTemplate, acquiringBankUrl);
  }

  @Test
  void shouldThrowClientExceptionOnRestClientException() {
    given(restTemplate.postForEntity(any(URI.class), any(AcquiringBankPaymentRequest.class), eq(
        AcquiringBankPaymentResponse.class))).willThrow(new RestClientException("dummy exception"));

    assertThatThrownBy(() -> underTest.makePaymentRequest(testClientRequest()))
        .isInstanceOf(AcquiringBankClientException.class)
        .hasMessage("An error occurred contacting the acquiring bank");
  }

  @Test
  void shouldHandleNon200ResponseFromAcquiringBank() throws Exception {
    ResponseEntity<AcquiringBankPaymentResponse> response = new ResponseEntity<>(
        null, HttpStatus.I_AM_A_TEAPOT
    );

    given(restTemplate.postForEntity(any(URI.class), any(AcquiringBankPaymentRequest.class), eq(
        AcquiringBankPaymentResponse.class))).willReturn(response);

    assertThatThrownBy(() -> underTest.makePaymentRequest(testClientRequest()))
        .isInstanceOf(AcquiringBankClientException.class)
        .hasMessage("Create payment failed with http status: 418");
  }

  @Test
  void shouldHandleValidRequest() throws Exception {
    UUID authorizationCode = UUID.randomUUID();
    ResponseEntity<AcquiringBankPaymentResponse> response = new ResponseEntity<>(
        new AcquiringBankPaymentResponse(true, authorizationCode), HttpStatus.OK
    );

    given(restTemplate.postForEntity(any(URI.class), any(AcquiringBankPaymentRequest.class), eq(
        AcquiringBankPaymentResponse.class))).willReturn(response);

    AcquiringBankPaymentResponse acquiringBankPaymentResponse = underTest.makePaymentRequest(testClientRequest());

    assertThat(acquiringBankPaymentResponse.authorized()).isTrue();
    assertThat(acquiringBankPaymentResponse.authorizationCode()).isEqualTo(authorizationCode);
  }

  private AcquiringBankPaymentRequest testClientRequest() {
    return new AcquiringBankPaymentRequest(
        "0000111122223333",
        "12/2030",
        "GBP",
        100,
        "123"
    );
  }
}
