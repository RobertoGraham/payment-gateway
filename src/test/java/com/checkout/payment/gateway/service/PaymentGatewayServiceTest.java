package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.client.AcquiringBankClient;
import com.checkout.payment.gateway.client.AcquiringBankPaymentRequest;
import com.checkout.payment.gateway.client.AcquiringBankPaymentResponse;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.PaymentNotFoundException;
import com.checkout.payment.gateway.model.CreatePaymentRequest;
import com.checkout.payment.gateway.model.PaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class PaymentGatewayServiceTest {

  private PaymentsRepository paymentsRepository;
  private AcquiringBankClient acquiringBankClient;
  private PaymentGatewayService underTest;

  @BeforeEach
  void setUp() {
    paymentsRepository = spy(new PaymentsRepository());
    acquiringBankClient = mock(AcquiringBankClient.class);
    underTest = new PaymentGatewayService(paymentsRepository, acquiringBankClient);
  }

  @Test
  void shouldReturnExistingPaymentById() throws Exception {
    UUID paymentId = UUID.randomUUID();
    given(acquiringBankClient.makePaymentRequest(any(
        AcquiringBankPaymentRequest.class))).willReturn(
        new AcquiringBankPaymentResponse(true, paymentId));

    underTest.processPayment(new CreatePaymentRequest(
        "1234123412341234",
        7,
        2026,
        "GBP",
        12345,
        "909"
    ));

    assertThat(underTest.getPaymentById(paymentId)).satisfies(payment -> {
      assertThat(payment.id()).isEqualTo(paymentId);
      assertThat(payment.status()).isEqualTo(PaymentStatus.AUTHORIZED);
      assertThat(payment.cardNumberLastFour()).isEqualTo(1234);
      assertThat(payment.expiryMonth()).isEqualTo(7);
      assertThat(payment.expiryYear()).isEqualTo(2026);
      assertThat(payment.currency()).isEqualTo("GBP");
      assertThat(payment.amount()).isEqualTo(12345);
    });
  }

  @Test
  void shouldThrowExceptionWhenPaymentIdDoesNotExist() {
    UUID paymentId = UUID.randomUUID();
    assertThatThrownBy(() -> underTest.getPaymentById(paymentId))
        .isInstanceOf(PaymentNotFoundException.class)
        .hasMessage(String.format("Payment with id %s not found", paymentId));
  }

  @Test
  void shouldSavePaymentToRepositoryWhenSuccessfulResponseFromAcquiringBank() throws Exception {
    UUID paymentId = UUID.randomUUID();
    given(acquiringBankClient.makePaymentRequest(any(
        AcquiringBankPaymentRequest.class))).willReturn(
        new AcquiringBankPaymentResponse(true, paymentId));

    CreatePaymentRequest paymentRequest = new CreatePaymentRequest(
        "1111222233334444",
        7,
        2026,
        "GBP",
        12345,
        "909"
    );

    PaymentResponse firstResponse = underTest.processPayment(paymentRequest);

    assertThat(firstResponse.id()).isEqualTo(paymentId);
    verify(paymentsRepository).add(argThat(paymentResponse -> {
      assertThat(paymentResponse.id()).isEqualTo(paymentId);
      assertThat(paymentResponse.paymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
      assertThat(paymentResponse.cardNumberLastFour()).isEqualTo(4444);
      assertThat(paymentResponse.expiryMonth()).isEqualTo(7);
      assertThat(paymentResponse.expiryYear()).isEqualTo(2026);
      assertThat(paymentResponse.currency()).isEqualTo("GBP");
      assertThat(paymentResponse.amount()).isEqualTo(12345);
      return true;
    }));
    verify(acquiringBankClient).makePaymentRequest(any(
        AcquiringBankPaymentRequest.class));
    verifyNoMoreInteractions(acquiringBankClient);
  }

  @Test
  void shouldCheckIfPaymentWasAlreadyProcessedBeforeCallingAcquiringBank() throws Exception {
    UUID paymentId = UUID.randomUUID();
    given(acquiringBankClient.makePaymentRequest(any(
        AcquiringBankPaymentRequest.class))).willReturn(
        new AcquiringBankPaymentResponse(true, paymentId));

    CreatePaymentRequest paymentRequest = new CreatePaymentRequest(
        "1234123412341234",
        7,
        2026,
        "GBP",
        12345,
        "909"
    );

    PaymentResponse firstResponse = underTest.processPayment(paymentRequest);
    PaymentResponse secondResponse = underTest.processPayment(paymentRequest);

    assertThat(firstResponse.id()).isEqualTo(paymentId);
    assertThat(secondResponse.id()).isEqualTo(paymentId);
    verify(acquiringBankClient).makePaymentRequest(any(
        AcquiringBankPaymentRequest.class));
    verifyNoMoreInteractions(acquiringBankClient);
  }


}
