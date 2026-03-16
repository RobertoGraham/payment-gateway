package paymentgateway.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.YearMonth;
import java.util.Currency;
import java.util.UUID;
import org.junit.jupiter.api.Test;

final class PaymentTests {

  @Test
  void idIsRequired() {
    assertThatThrownBy(() -> Payment.builder()
        .card(MaskedCard.builder()
            .last4Digits("0123")
            .expiry(YearMonth.now().plusMonths(1L))
            .build())
        .amount(MonetaryAmount.builder()
            .currency(Currency.getInstance("USD"))
            .value(1L)
            .build())
        .status(PaymentStatus.AUTHORIZED)
        .build())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("id is marked non-null but is null");
  }

  @Test
  void amountIsRequired() {
    assertThatThrownBy(() -> Payment.builder()
        .card(MaskedCard.builder()
            .last4Digits("0123")
            .expiry(YearMonth.now().plusMonths(1L))
            .build())
        .id(PaymentId.builder()
            .value(UUID.randomUUID())
            .build())
        .status(PaymentStatus.AUTHORIZED)
        .build())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("amount is marked non-null but is null");
  }

  @Test
  void cardIsRequired() {
    assertThatThrownBy(() -> Payment.builder()
        .amount(MonetaryAmount.builder()
            .currency(Currency.getInstance("USD"))
            .value(1L)
            .build())
        .id(PaymentId.builder()
            .value(UUID.randomUUID())
            .build())
        .status(PaymentStatus.AUTHORIZED)
        .build())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("card is marked non-null but is null");
  }

  @Test
  void statusIsRequired() {
    assertThatThrownBy(() -> Payment.builder()
        .amount(MonetaryAmount.builder()
            .currency(Currency.getInstance("USD"))
            .value(1L)
            .build())
        .id(PaymentId.builder()
            .value(UUID.randomUUID())
            .build())
        .card(MaskedCard.builder()
            .last4Digits("0123")
            .expiry(YearMonth.now().plusMonths(1L))
            .build())
        .build())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("status is marked non-null but is null");
  }

  @Test
  void valid() {
    assertThat(Payment.builder()
        .id(PaymentId.builder()
            .value(UUID.randomUUID())
            .build())
        .card(MaskedCard.builder()
            .last4Digits("0123")
            .expiry(YearMonth.now().plusMonths(1L))
            .build())
        .amount(MonetaryAmount.builder()
            .currency(Currency.getInstance("USD"))
            .value(1L)
            .build())
        .status(PaymentStatus.AUTHORIZED)
        .build())
        .isNotNull();
  }
}
