package paymentgateway.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Currency;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import paymentgateway.domain.exception.DomainValidationException;

final class MonetaryAmountTests {

  private static Stream<Arguments> unacceptableCurrencies() {
    return Currency.availableCurrencies()
        .filter(Predicate.not(MonetaryAmount.ACCEPTABLE_CURRENCIES::contains))
        .map(Arguments::of);
  }

  @Test
  void currencyIsRequired() {
    assertThatThrownBy(() -> MonetaryAmount.builder()
        .value(1L)
        .build())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("currency is marked non-null but is null");
  }

  @ValueSource(longs = {-1L, 0L})
  @ParameterizedTest
  void valueMustBePositive(final long value) {
    assertThatThrownBy(() -> MonetaryAmount.builder()
        .value(value)
        .currency(Currency.getInstance("USD"))
        .build())
        .isInstanceOf(DomainValidationException.class)
        .hasMessage("value must be positive");
  }

  @MethodSource("unacceptableCurrencies")
  @ParameterizedTest
  void currencyMustBeAccepted(final Currency currency) {
    assertThatThrownBy(() -> MonetaryAmount.builder()
        .value(1L)
        .currency(currency)
        .build())
        .isInstanceOf(DomainValidationException.class)
        .hasMessage("currency must be accepted");
  }

  @ValueSource(strings = {"USD", "EUR", "GBP"})
  @ParameterizedTest
  void valid(final Currency currency) {
    assertThat(MonetaryAmount.builder()
        .value(1L)
        .currency(currency)
        .build())
        .isNotNull();
  }
}
