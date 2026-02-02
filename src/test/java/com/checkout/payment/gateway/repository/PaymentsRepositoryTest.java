package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.CreatePaymentRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentsRepositoryTest {

  private PaymentsRepository underTest = new PaymentsRepository();

  @Test
  void shouldReturnEmptyOptionalWhenTryingToGetByNonExistentId() {
    assertThat(underTest.get(UUID.randomUUID())).isEmpty();
  }

  @Test
  void shouldReturnOptionalPaymentWhenPaymentExistsById() {
    UUID paymentId = UUID.randomUUID();
    ProcessedPayment response = new ProcessedPayment(
        paymentId,
        PaymentStatus.AUTHORIZED,
        5678,
        12,
        2025,
        "USD",
        5000
    );

    underTest.add(response);

    assertThat(underTest.get(paymentId)).satisfies(optionalPayment -> {
      assertThat(optionalPayment).isPresent();
      assertThat(optionalPayment.get().id()).isEqualTo(paymentId);
    });
  }

  @Test
  void shouldReturnEmptyOptionalWhenNoPaymentsExists() {
    assertThat(underTest.exists(null)).isEmpty();
  }

  @Test
  void shouldReturnEmptyOptionalWhenPaymentDetailsDoNotMatch() {
    ProcessedPayment response = new ProcessedPayment(
        UUID.randomUUID(),
        PaymentStatus.AUTHORIZED,
        1234,
        7,
        2026,
        "GBP",
        12345
    );

    underTest.add(response);

    assertThat(underTest.exists(new CreatePaymentRequest(
        "4321432143214321",
        7,
        2026,
        "GBP",
        12345,
        "123"
    ))).isEmpty();
  }

  @Test
  void shouldReturnOptionalPaymentWhenPaymentExists() {
    UUID paymentId = UUID.randomUUID();
    ProcessedPayment response = new ProcessedPayment(
        paymentId,
        PaymentStatus.AUTHORIZED,
        4321,
        4,
        2026,
        "EUR",
        8189
    );

    underTest.add(response);

    assertThat(underTest.exists(new CreatePaymentRequest(
        "4321432143214321",
        4,
        2026,
        "EUR",
        8189,
        "123"
    ))).satisfies(optionalPayment -> {
      assertThat(optionalPayment).isPresent();
      assertThat(optionalPayment.get().id()).isEqualTo(paymentId);
    });
  }
}