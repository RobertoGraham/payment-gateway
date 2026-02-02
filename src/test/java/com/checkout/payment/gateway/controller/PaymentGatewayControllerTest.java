package com.checkout.payment.gateway.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.CreatePaymentRequest;
import com.checkout.payment.gateway.model.PaymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.time.YearMonth;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldGetPaymentByIdWillReturn404() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/payment/{id}", UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldGetPaymentByIdWhenPaymentExists() throws Exception {
    CreatePaymentRequest paymentRequest = createTestRequest("6666777788889999");

    MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/payment/create")
            .content(objectMapper.writeValueAsString(paymentRequest))
            .contentType("application/json"))
        .andExpect(status().isOk())
        .andReturn();

    PaymentResponse paymentResponse = objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(), PaymentResponse.class);

    mvc.perform(MockMvcRequestBuilders.get("/payment/{id}", paymentResponse.id()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(paymentResponse.id().toString()))
        .andExpect(jsonPath("$.status").value(paymentResponse.status().getName()));
  }

  @Test
  void shouldReturnAuthorizedStatusWhenAuthorizedByAcquirer() throws Exception {
    CreatePaymentRequest paymentRequest = createTestRequest("6666777788889999");

    mvc.perform(MockMvcRequestBuilders.post("/payment/create")
            .content(objectMapper.writeValueAsString(paymentRequest))
            .contentType("application/json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
    ;
  }

  @Test
  void shouldReturnDeclinedStatusWhenDeclinedByAcquirer() throws Exception {
    CreatePaymentRequest paymentRequest = createTestRequest("5555444433332222");

    mvc.perform(MockMvcRequestBuilders.post("/payment/create")
            .content(objectMapper.writeValueAsString(paymentRequest))
            .contentType("application/json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(PaymentStatus.DECLINED.getName()))
    ;
  }

  @Test
  void shouldRejectPaymentWithInvalidInformation() throws Exception {
    CreatePaymentRequest paymentRequest = createTestRequest("123456789");

    mvc.perform(MockMvcRequestBuilders.post("/payment/create")
            .content(objectMapper.writeValueAsString(paymentRequest))
            .contentType("application/json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(PaymentStatus.REJECTED.getName()))
    ;
  }

  @Test
  void shouldHandleIdempotentRequest() throws Exception {
    CreatePaymentRequest paymentRequest = createTestRequest("6666777788889999");
    MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/payment/create")
            .content(objectMapper.writeValueAsString(paymentRequest))
            .contentType("application/json"))
        .andReturn();

    PaymentResponse paymentResponse = objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(), PaymentResponse.class);
    assertThat(paymentResponse.status()).isEqualTo(PaymentStatus.AUTHORIZED);
    UUID previousPaymentId = paymentResponse.id();

    mvc.perform(MockMvcRequestBuilders.post("/payment/create")
            .content(objectMapper.writeValueAsString(paymentRequest))
            .contentType("application/json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.id").value(previousPaymentId.toString()))
    ;
  }

  @Test
  void shouldHandleNon200ResponseFromAcquirer() throws Exception {
    CreatePaymentRequest paymentRequest = createTestRequest("1111222233334440");
    mvc.perform(MockMvcRequestBuilders.post("/payment/create")
            .content(objectMapper.writeValueAsString(paymentRequest))
            .contentType("application/json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(PaymentStatus.REJECTED.getName()))
    ;
  }

  private CreatePaymentRequest createTestRequest(String cardNumber) {
    YearMonth yearMonth = YearMonth.now().plusMonths(2);
    return new CreatePaymentRequest(
        cardNumber,
        yearMonth.getMonthValue(),
        yearMonth.getYear(),
        "USD",
        300,
        "123"
    );
  }
}
