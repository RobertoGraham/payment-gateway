package paymentgateway.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigInteger;
import java.util.Currency;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

final class MonetaryAmountTests {

  private static Stream<Arguments> unacceptableCurrencies() {
    return Currency.availableCurrencies()
        .filter(Predicate.not(MonetaryAmount.ACCEPTABLE_CURRENCIES::contains))
        .map(Arguments::of);
  }

  @Test
  void valueIsRequired() {
    assertThatThrownBy(() -> MonetaryAmount.builder()
        .currency(Currency.getInstance("USD"))
        .build())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("value is marked non-null but is null");
  }

  @Test
  void currencyIsRequired() {
    assertThatThrownBy(() -> MonetaryAmount.builder()
        .value(BigInteger.ONE)
        .build())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("currency is marked non-null but is null");
  }

  @ValueSource(strings = {"-1", "0"})
  @ParameterizedTest
  void valueMustBePositive(final BigInteger value) {
    assertThatThrownBy(() -> MonetaryAmount.builder()
        .value(value)
        .currency(Currency.getInstance("USD"))
        .build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("value must be positive");
  }

  @MethodSource("unacceptableCurrencies")
  @ParameterizedTest
  void currencyMustBeAccepted(final Currency currency) {
    assertThatThrownBy(() -> MonetaryAmount.builder()
        .value(BigInteger.ONE)
        .currency(currency)
        .build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("currency must be accepted");
  }

  @ValueSource(strings = {"USD", "EUR", "GBP"})
  @ParameterizedTest
  void valid(final Currency currency) {
    assertThat(MonetaryAmount.builder()
        .value(BigInteger.ONE)
        .currency(currency)
        .build())
        .isNotNull();
  }
}
