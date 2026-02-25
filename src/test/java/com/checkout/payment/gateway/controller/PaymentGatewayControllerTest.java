package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  PaymentsRepository paymentsRepository;

  ObjectMapper mapper = new ObjectMapper();

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    PostPaymentResponse payment = new PostPaymentResponse();
    payment.setId(UUID.randomUUID());
    payment.setAmount(10);
    payment.setCurrency("USD");
    payment.setStatus(PaymentStatus.AUTHORIZED);
    payment.setExpiryMonth(12);
    payment.setExpiryYear(2024);
    payment.setCardNumberLastFour(4321);

    paymentsRepository.add(payment);

    mvc.perform(MockMvcRequestBuilders.get("/payment/" + payment.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(payment.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.getCurrency()))
        .andExpect(jsonPath("$.amount").value(payment.getAmount()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Page not found"));
  }

  @Test
  void whenCorrectPaymentRequestSubmittedThenCorrectPaymentIsStoredAndReturned() throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setAmount(20000);
    request.setCurrency("GBP");
    request.setExpiryMonth(8);
    request.setExpiryYear(2027);
    request.setCardNumber("2222405343248877");
    request.setCvv(123);

    String json = mapper.writeValueAsString(request);
    MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/payment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(notNullValue()))
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(8877))
        .andExpect(jsonPath("$.expiryMonth").value(request.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(request.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(request.getCurrency()))
        .andExpect(jsonPath("$.amount").value(request.getAmount()))
        .andReturn();

      json = result.getResponse().getContentAsString();
      PostPaymentResponse response = mapper.readValue(json, PostPaymentResponse.class);
      UUID id = response.getId();

      mvc.perform(MockMvcRequestBuilders.get("/payment/" + id))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(notNullValue()))
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(8877))
        .andExpect(jsonPath("$.expiryMonth").value(request.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(2027))
        .andExpect(jsonPath("$.currency").value(request.getCurrency()))
        .andExpect(jsonPath("$.amount").value(request.getAmount()));
  }

  @Test
  public void whenCorrectPaymentRequestSubmittedWithInvalidCardThenPaymentIsNotStoredAndDeclinedStatusIsReturned()
      throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setAmount(20000);
    request.setCurrency("GBP");
    request.setExpiryMonth(8);
    request.setExpiryYear(2027);
    request.setCardNumber("2222405343248874");
    request.setCvv(123);

    String json = mapper.writeValueAsString(request);
    MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/payment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(nullValue()))
        .andExpect(jsonPath("$.status").value(PaymentStatus.DECLINED.getName()))
        .andReturn();
  }

  @Test
  public void whenGarbagePaymentRequestSubmittedThenGarbagePaymentDataIsReturned()
      throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setAmount(000000000);
    request.setCurrency("TNR");
    request.setExpiryMonth(13);
    request.setExpiryYear(30597);
    request.setCardNumber("2222405343248874");
    request.setCvv(34567);

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(request);
    MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(nullValue()))
        .andExpect(jsonPath("$.status").value(PaymentStatus.DECLINED.getName()))
        .andReturn();
  }
}
