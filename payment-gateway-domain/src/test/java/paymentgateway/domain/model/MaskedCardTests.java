package paymentgateway.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

final class MaskedCardTests {

  @Test
  void last4DigitsIsRequired() {
    assertThatThrownBy(() -> MaskedCard.builder()
        .expiry(YearMonth.now().plusMonths(1L))
        .build())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("last4Digits is marked non-null but is null");
  }

  @Test
  void expiryIsRequired() {
    assertThatThrownBy(() -> MaskedCard.builder()
        .last4Digits("0123")
        .build())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("expiry is marked non-null but is null");
  }

  @ValueSource(strings = {"abcd", "", " ", "1", "12345"})
  @ParameterizedTest
  void last4DigitsMustBeValid(final String last4Digits) {
    assertThatThrownBy(() -> MaskedCard.builder()
        .last4Digits(last4Digits)
        .expiry(YearMonth.now().plusMonths(1L))
        .build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("last4Digits must only contain numeric characters and be 4 characters");
  }

  @Test
  void valid() {
    assertThat(MaskedCard.builder()
        .last4Digits("0123")
        .expiry(YearMonth.parse("1996-12"))
        .build())
        .isNotNull();
  }
}
