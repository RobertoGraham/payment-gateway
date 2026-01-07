package com.checkout.payment.gateway.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.client.AcquiringBankClient;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.time.YearMonth;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.HttpServerErrorException;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  private static final String AUTHORISED_CARD_NUMBER = "4000000000000001";
  private static final String DECLINED_CARD_NUMBER = "4000000000000002";

  @Autowired
  private MockMvc mvc;

  @Autowired
  private PaymentsRepository paymentsRepository;

  @MockBean
  private AcquiringBankClient acquiringBankClient;

  @Test
  void getPostPaymentEventById_whenExists_returns200AndPaymentDetails() throws Exception {
    UUID existingPaymentId = UUID.randomUUID();
    PostPaymentResponse payment = buildPaymentResponse(existingPaymentId);
    paymentsRepository.add(payment);

    ResultActions result = mvc.perform(get("/payment/{id}", existingPaymentId));

    result.andExpect(status().isOk());
    assertPaymentResponseDetails(result, payment);

    verify(acquiringBankClient, never()).requestAuthorization(any());
  }

  @Test
  void getPostPaymentEventById_whenMissing_returns404AndErrorMessageContainsId() throws Exception {
    UUID missingPaymentId = UUID.randomUUID();

    mvc.perform(get("/payment/{id}", missingPaymentId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", containsString(missingPaymentId.toString())));

    verify(acquiringBankClient, never()).requestAuthorization(any());
  }

  @Test
  void processPayment_whenBankAuthorizes_savesPaymentAndCanBeRetrieved() throws Exception {
    when(acquiringBankClient.requestAuthorization(any())).thenReturn(bankResponse(true));

    String requestBody = buildValidPostPaymentRequestJson(AUTHORISED_CARD_NUMBER);

    ResultActions postResult = postPayment(requestBody)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty());

    String id = postResult.andReturn().getResponse().getContentAsString()
        .replaceAll(".*\"id\"\\s*:\\s*\"([^\"]+)\".*", "$1");

    ResultActions getResult = mvc.perform(get("/payment/{id}", id))
        .andExpect(status().isOk());

    getResult.andExpect(jsonPath("$.cardNumberLastFour").value("0001"));
  }

  @Test
  void processPayment_whenCallingBank_sendsMappedBankRequest() throws Exception {
    when(acquiringBankClient.requestAuthorization(any())).thenReturn(bankResponse(true));

    YearMonth now = YearMonth.now();
    postPayment(buildValidPostPaymentRequestJson(AUTHORISED_CARD_NUMBER))
        .andExpect(status().isOk());

    ArgumentCaptor<BankPaymentRequest> captor = ArgumentCaptor.forClass(BankPaymentRequest.class);
    verify(acquiringBankClient).requestAuthorization(captor.capture());

    BankPaymentRequest sent = captor.getValue();
    assertThat(sent.getCardNumber()).isEqualTo(AUTHORISED_CARD_NUMBER);
    assertThat(sent.getExpiryDate()).isEqualTo(
        String.format("%02d/%d", now.getMonthValue(), now.getYear()));
    assertThat(sent.getCurrency()).isEqualTo("GBP");
    assertThat(sent.getAmount()).isEqualTo(99);
    assertThat(sent.getCvv()).isEqualTo("123");
  }

  @Test
  void processPayment_whenBankAuthorizes_returns200AndAuthorizedPayment() throws Exception {
    when(acquiringBankClient.requestAuthorization(any())).thenReturn(bankResponse(true));

    YearMonth now = YearMonth.now();
    String requestBody = buildValidPostPaymentRequestJson(AUTHORISED_CARD_NUMBER);
    PostPaymentResponse expected = expectedResponse(PaymentStatus.AUTHORIZED, "0001", now);

    ResultActions result = postPayment(requestBody);

    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty());

    assertPaymentResponseDetails(result, expected);

    verify(acquiringBankClient, times(1)).requestAuthorization(any());
  }

  @Test
  void processPayment_whenBankDeclines_returns200AndDeclinedPayment() throws Exception {
    when(acquiringBankClient.requestAuthorization(any())).thenReturn(bankResponse(false));

    YearMonth now = YearMonth.now();
    String requestBody = buildValidPostPaymentRequestJson(DECLINED_CARD_NUMBER);
    PostPaymentResponse expected = expectedResponse(PaymentStatus.DECLINED, "0002", now);

    ResultActions result = postPayment(requestBody);

    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty());

    assertPaymentResponseDetails(result, expected);

    verify(acquiringBankClient, times(1)).requestAuthorization(any());
  }

  @Test
  void processPayment_whenBankUnavailable_returns200AndDeclinedPayment() throws Exception {
    when(acquiringBankClient.requestAuthorization(any()))
        .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE));

    YearMonth now = YearMonth.now();
    String requestBody = buildValidPostPaymentRequestJson(AUTHORISED_CARD_NUMBER);
    PostPaymentResponse expected = expectedResponse(PaymentStatus.DECLINED, "0001", now);

    ResultActions result = postPayment(requestBody);

    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty());

    assertPaymentResponseDetails(result, expected);

    verify(acquiringBankClient, times(1)).requestAuthorization(any());
  }

  @Test
  void processPayment_whenAuthorized_responseBodyDoesNotContainFullCardNumber() throws Exception {
    when(acquiringBankClient.requestAuthorization(any())).thenReturn(bankResponse(true));

    String requestBody = buildValidPostPaymentRequestJson(AUTHORISED_CARD_NUMBER);

    ResultActions result = postPayment(requestBody);

    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.cardNumberLastFour").value("0001"));

    String responseBody = result.andReturn().getResponse().getContentAsString();
    assertThat(responseBody).doesNotContain(AUTHORISED_CARD_NUMBER);
  }

  @Test
  void processPayment_whenValidationFails_returns400Rejected_andDoesNotCallBank() throws Exception {
    String invalidBody = """
        {
          "expiry_month": 12,
          "expiry_year": 2099,
          "currency": "GBP",
          "amount": 99,
          "cvv": "123"
        }
        """;

    postPayment(invalidBody)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Rejected"));

    verify(acquiringBankClient, never()).requestAuthorization(any());
  }

  @Test
  void processPayment_whenRequestBodyMalformed_returns400Rejected_andDoesNotCallBank()
      throws Exception {

    postPayment("{")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Rejected"));

    verify(acquiringBankClient, never()).requestAuthorization(any());
  }

  private ResultActions postPayment(String requestBody) throws Exception {
    return mvc.perform(
        post("/payment")
            .contentType(APPLICATION_JSON)
            .content(requestBody)
    );
  }

  private static BankPaymentResponse bankResponse(boolean authorized) {
    BankPaymentResponse response = new BankPaymentResponse();
    response.setAuthorized(authorized);
    response.setAuthorizationCode(authorized ? "authorizationCode" : "");
    return response;
  }

  private static PostPaymentResponse expectedResponse(PaymentStatus status, String last4,
      YearMonth now) {
    PostPaymentResponse expected = new PostPaymentResponse();
    expected.setStatus(status);
    expected.setCardNumberLastFour(last4);
    expected.setExpiryMonth(now.getMonthValue());
    expected.setExpiryYear(now.getYear());
    expected.setCurrency("GBP");
    expected.setAmount(99);
    return expected;
  }

  private static String buildValidPostPaymentRequestJson(String cardNumber) {
    YearMonth now = YearMonth.now();
    return """
        {
          "card_number": "%s",
          "expiry_month": %d,
          "expiry_year": %d,
          "currency": "GBP",
          "amount": 99,
          "cvv": "123"
        }
        """.formatted(cardNumber, now.getMonthValue(), now.getYear());
  }

  private static PostPaymentResponse buildPaymentResponse(UUID id) {
    PostPaymentResponse payment = new PostPaymentResponse();
    payment.setId(id);
    payment.setStatus(PaymentStatus.AUTHORIZED);
    payment.setCardNumberLastFour("4321");
    payment.setExpiryMonth(12);
    payment.setExpiryYear(2099);
    payment.setCurrency("USD");
    payment.setAmount(10);
    return payment;
  }

  private static void assertPaymentResponseDetails(ResultActions result,
      PostPaymentResponse expected) throws Exception {

    result
        .andExpect(jsonPath("$.status").value(expected.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(expected.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(expected.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(expected.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(expected.getCurrency()))
        .andExpect(jsonPath("$.amount").value(expected.getAmount()));
  }
}
