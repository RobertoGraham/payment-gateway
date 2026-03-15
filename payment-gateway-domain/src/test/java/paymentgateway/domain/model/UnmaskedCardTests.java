package paymentgateway.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

final class UnmaskedCardTests {

  @Test
  void numberIsRequired() {
    assertThatThrownBy(() -> UnmaskedCard.builder()
        .expiry(YearMonth.now().plusMonths(1L))
        .securityCode("123")
        .build())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("number is marked non-null but is null");
  }

  @Test
  void expiryIsRequired() {
    assertThatThrownBy(() -> UnmaskedCard.builder()
        .number("01234567898765")
        .securityCode("123")
        .build())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("expiry is marked non-null but is null");
  }

  @Test
  void securityCodeIsRequired() {
    assertThatThrownBy(() -> UnmaskedCard.builder()
        .number("01234567898765")
        .expiry(YearMonth.now().plusMonths(1L))
        .build())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("securityCode is marked non-null but is null");
  }

  @ValueSource(strings = {"abcdefghijklmn", "", " ", "1", "01234567890123456789"})
  @ParameterizedTest
  void numberMustBeValid(final String number) {
    assertThatThrownBy(() -> UnmaskedCard.builder()
        .number(number)
        .expiry(YearMonth.now().plusMonths(1L))
        .securityCode("123")
        .build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("number must only contain numeric characters and be between 14-19 characters");
  }

  @Test
  void expiryMustBeInTheFuture() {
    assertThatThrownBy(() -> UnmaskedCard.builder()
        .number("01234567898765")
        .expiry(YearMonth.parse("1996-12"))
        .securityCode("123")
        .build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("expiry must be in the future");
  }

  @ValueSource(strings = {"", " ", "a", "12", "12345"})
  @ParameterizedTest
  void securityCodeMustBeValid(final String securityCode) {
    assertThatThrownBy(() -> UnmaskedCard.builder()
        .number("01234567898765")
        .expiry(YearMonth.now().plusMonths(1L))
        .securityCode(securityCode)
        .build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "securityCode must only contain numeric characters and be between 3-4 characters");
  }

  @Test
  void valid() {
    assertThat(UnmaskedCard.builder()
        .number("01234567898765")
        .expiry(YearMonth.now().plusMonths(1L))
        .securityCode("123")
        .build())
        .isNotNull();
  }

  @Test
  void toStringDoesNotLeakSensitiveData() {
    assertThat(UnmaskedCard.builder()
        .number("01234567898765")
        .expiry(YearMonth.now().plusMonths(1L))
        .securityCode("123")
        .build())
        .hasToString("PaymentCard{expiry=%s}".formatted(YearMonth.now().plusMonths(1L)));
  }
}
