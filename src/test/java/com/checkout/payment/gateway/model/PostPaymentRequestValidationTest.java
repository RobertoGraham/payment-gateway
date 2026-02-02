package com.checkout.payment.gateway.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.YearMonth;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PostPaymentRequestValidationTest {

  @Test
  void shouldPassValidationForValidRequest() {
    YearMonth yearMonth = YearMonth.now().plusMonths(2);
    CreatePaymentRequest request = new CreatePaymentRequest(
        "0000111122223333",
        yearMonth.getMonthValue(),
        yearMonth.getYear(),
        "USD",
        100,
        "555"
    );

    assertNoValidationFailures(request);
  }

  @MethodSource("invalidCardNumbersProvider")
  @ParameterizedTest
  void shouldFailValidationForInvalidCardNumber(String cardNumber, String expectedMessage) {
    YearMonth yearMonth = YearMonth.now().plusMonths(2);
    CreatePaymentRequest request = new CreatePaymentRequest(
        cardNumber,
        yearMonth.getMonthValue(),
        yearMonth.getYear(),
        "USD",
        100,
        "555"
    );

    assertValidationFailureMessage(request, expectedMessage);
  }

  @Test
  void shouldFailValidationForNullExpiryMonth() {
    CreatePaymentRequest request = new CreatePaymentRequest(
        "0000111122223333",
        null,
        2027,
        "USD",
        100,
        "555"
    );

    assertValidationFailureMessage(request, "Expiry month must not be null");
  }

  @Test
  void shouldFailValidationForNullExpiryYear() {
    CreatePaymentRequest request = new CreatePaymentRequest(
        "0000111122223333",
        4,
        null,
        "USD",
        100,
        "555"
    );

    assertValidationFailureMessage(request, "Expiry year must not be null");
  }

  @Test
  void shouldFailValidationForInvalidYearMonth() {
    CreatePaymentRequest request = new CreatePaymentRequest(
        "0000111122223333",
        11,
        2010,
        "USD",
        100,
        "555"
    );

    assertValidationFailureMessage(request, "Expiry date must be in the future");
  }

  @ValueSource(strings = {"US", "GBPA"})
  @ParameterizedTest
  void shouldFailValidationWhenCurrencyCodeLengthInvalid(String currencyCode) {
    YearMonth yearMonth = YearMonth.now().plusMonths(2);
    CreatePaymentRequest request = new CreatePaymentRequest(
        "0000111122223333",
        yearMonth.getMonthValue(),
        yearMonth.getYear(),
        currencyCode,
        100,
        "555"
    );

    assertValidationFailureMessage(request, "Currency must be a 3-letter ISO currency code");
  }

  @Test
  void shouldFailValidationWhenCurrencyCodeNotRecognised() {
    YearMonth yearMonth = YearMonth.now().plusMonths(2);
    CreatePaymentRequest request = new CreatePaymentRequest(
        "0000111122223333",
        yearMonth.getMonthValue(),
        yearMonth.getYear(),
        "YSD",
        100,
        "555"
    );

    assertValidationFailureMessage(request, "Unrecognised currency code provided");
  }

  @Test
  void shouldFailValidationForNullCvv() {
    YearMonth yearMonth = YearMonth.now().plusMonths(2);
    CreatePaymentRequest request = new CreatePaymentRequest(
        "0000111122223333",
        yearMonth.getMonthValue(),
        yearMonth.getYear(),
        "USD",
        100,
        null
    );

    assertValidationFailureMessage(request, "CVV must not be null");
  }

  @MethodSource("invalidCvvNumbersProvider")
  @ParameterizedTest
  void shouldFailValidationForIncorrectLengthCvv(String cvv, String expectedMessage) {
    CreatePaymentRequest request = new CreatePaymentRequest(
        "0000111122223333",
        11,
        2026,
        "USD",
        100,
        cvv
    );

    assertValidationFailureMessage(request, expectedMessage);
  }

  private static Stream<Arguments> invalidCardNumbersProvider() {
    return Stream.of(
        Arguments.of("1234abcd5678efgh", "Card number must contain only digits"),
        Arguments.of("1234567890123", "Card number must be between 14 and 19 digits"),
        Arguments.of("12345678901234567890", "Card number must be between 14 and 19 digits")
    );
  }

  private static Stream<Arguments> invalidCvvNumbersProvider() {
    return Stream.of(
        Arguments.of("12", "CVV must be between 3 and 4 digits"),
        Arguments.of("12345", "CVV must be between 3 and 4 digits"),
        Arguments.of("12a", "CVV must contain only digits")
    );
  }

  private void assertNoValidationFailures(CreatePaymentRequest request) {
    try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      Validator validator = validatorFactory.getValidator();
      Set<ConstraintViolation<CreatePaymentRequest>> constraintViolations = validator.validate(
          request);

      assertThat(constraintViolations).isEmpty();
    }
  }

  private void assertValidationFailureMessage(CreatePaymentRequest request, String expectedMessage) {
    try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      Validator validator = validatorFactory.getValidator();
      Set<ConstraintViolation<CreatePaymentRequest>> constraintViolations = validator.validate(
          request);

      assertThat(constraintViolations).satisfies(violations -> {
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(expectedMessage);
      });
    }
  }

}
