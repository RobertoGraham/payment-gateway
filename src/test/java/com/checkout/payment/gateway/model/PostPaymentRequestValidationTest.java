package com.checkout.payment.gateway.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.checkout.payment.gateway.validation.ValidExpiryValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.YearMonth;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PostPaymentRequestValidationTest {

  private static ValidatorFactory factory;
  private static Validator validator;

  @BeforeAll
  static void setUpValidator() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  static void tearDownValidator() {
    factory.close();
  }

  @Test
  void cardNumber_whenMissing_hasNotBlankViolation() {
    PostPaymentRequest req = validRequest();
    req.setCardNumber(null);

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "cardNumber", "card_number is required");
  }

  @Test
  void cardNumber_whenBlank_hasNotBlankViolation() {
    PostPaymentRequest req = validRequest();
    req.setCardNumber("   ");

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "cardNumber", "card_number is required");
  }

  @Test
  void cardNumber_whenInvalidFormat_hasPatternViolation() {
    PostPaymentRequest req = validRequest();
    req.setCardNumber("1234abcd");

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "cardNumber", "card_number must be 14-19 digits and numeric");
  }

  @Test
  void cardNumber_whenTooShort_hasPatternViolation() {
    PostPaymentRequest req = validRequest();
    req.setCardNumber("4000000000000");

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "cardNumber", "card_number must be 14-19 digits and numeric");
  }

  @Test
  void cardNumber_whenTooLong_hasPatternViolation() {
    PostPaymentRequest req = validRequest();
    req.setCardNumber("40000000000000000000");

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "cardNumber", "card_number must be 14-19 digits and numeric");
  }

  @Test
  void cardNumber_when14Digits_isValid() {
    PostPaymentRequest req = validRequest();
    req.setCardNumber("40000000000000");

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertThat(violations).isEmpty();
  }

  @Test
  void cardNumber_when19Digits_isValid() {
    PostPaymentRequest req = validRequest();
    req.setCardNumber("4000000000000000001");

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertThat(violations).isEmpty();
  }

  @Test
  void expiryMonth_whenNull_hasNotNullViolation() {
    PostPaymentRequest req = validRequest();
    req.setExpiryMonth(null);

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "expiryMonth", "expiry_month is required");
  }

  @Test
  void expiryMonth_whenZero_hasMinMaxViolation() {
    PostPaymentRequest req = validRequest();
    req.setExpiryMonth(0);

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "expiryMonth", "expiry_month must be between 1 and 12");
  }

  @Test
  void expiryMonth_whenOutOfRange_hasMinMaxViolation() {
    PostPaymentRequest req = validRequest();
    req.setExpiryMonth(13);

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "expiryMonth", "expiry_month must be between 1 and 12");
  }

  @Test
  void expiryYear_whenNull_hasNotNullViolation() {
    PostPaymentRequest req = validRequest();
    req.setExpiryYear(null);

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "expiryYear", "expiry_year is required");
  }

  @Test
  void expiry_whenBothNull_hasBothNotNullViolations() {
    PostPaymentRequest req = validRequest();
    req.setExpiryMonth(null);
    req.setExpiryYear(null);

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("expiryMonth"));
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("expiryYear"));
  }

  @Test
  void expiryCombination_whenExpired_hasValidExpiryViolation() {
    PostPaymentRequest req = validRequest();
    YearMonth expired = YearMonth.now().minusMonths(1);
    req.setExpiryMonth(expired.getMonthValue());
    req.setExpiryYear(expired.getYear());

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertThat(violations).anyMatch(v ->
        v.getMessage().equals("expiry_month/expiry_year must be in the future"));
  }

  @Test
  void expiryCombination_whenCurrentMonth_isValid() {
    PostPaymentRequest req = validRequest();
    YearMonth now = YearMonth.now();
    req.setExpiryMonth(now.getMonthValue());
    req.setExpiryYear(now.getYear());

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertThat(violations).isEmpty();
  }

  @Test
  void currency_whenMissing_hasNotBlankViolation() {
    PostPaymentRequest req = validRequest();
    req.setCurrency(null);

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "currency", "currency is required");
  }

  @Test
  void currency_whenLowercase_isValid() {
    PostPaymentRequest req = validRequest();
    req.setCurrency("gbp");

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertThat(violations).isEmpty();
  }

  @Test
  void currency_whenUnsupported_hasPatternViolation() {
    PostPaymentRequest req = validRequest();
    req.setCurrency("JPY");

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "currency", "currency must be one of [GBP, USD, EUR]");
  }

  @Test
  void amount_whenNull_hasNotNullViolation() {
    PostPaymentRequest req = validRequest();
    req.setAmount(null);

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "amount", "amount is required");
  }

  @Test
  void amount_whenZero_hasMinViolation() {
    PostPaymentRequest req = validRequest();
    req.setAmount(0);

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "amount", "amount must be a positive integer");
  }

  @Test
  void amount_whenNegative_hasMinViolation() {
    PostPaymentRequest req = validRequest();
    req.setAmount(-1);

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "amount", "amount must be a positive integer");
  }

  @Test
  void cvv_whenMissing_hasNotBlankViolation() {
    PostPaymentRequest req = validRequest();
    req.setCvv(null);

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "cvv", "cvv is required");
  }

  @Test
  void cvv_whenInvalidFormat_hasPatternViolation() {
    PostPaymentRequest req = validRequest();
    req.setCvv("12");

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "cvv", "cvv must be 3-4 digits and numeric");
  }

  @Test
  void cvv_whenTooLong_hasPatternViolation() {
    PostPaymentRequest req = validRequest();
    req.setCvv("12345");

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertHasViolation(violations, "cvv", "cvv must be 3-4 digits and numeric");
  }

  @Test
  void cvv_whenFourDigits_isValid() {
    PostPaymentRequest req = validRequest();
    req.setCvv("1234");

    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(req);

    assertThat(violations).isEmpty();
  }

  @Test
  void whenEntireRequestIsNull_isValid() {
    ValidExpiryValidator customValidator = new ValidExpiryValidator();
    boolean result = customValidator.isValid(null, null);
    assertThat(result).isTrue();
  }

  private static void assertHasViolation(
      Set<ConstraintViolation<PostPaymentRequest>> violations,
      String propertyPath,
      String message
  ) {
    assertThat(violations).anyMatch(v ->
        v.getPropertyPath().toString().equals(propertyPath)
            && v.getMessage().equals(message));
  }

  private static PostPaymentRequest validRequest() {
    YearMonth now = YearMonth.now();

    PostPaymentRequest req = new PostPaymentRequest();
    req.setCardNumber("4000000000000001");
    req.setExpiryMonth(now.getMonthValue());
    req.setExpiryYear(now.getYear());
    req.setCurrency("GBP");
    req.setAmount(99);
    req.setCvv("123");
    return req;
  }
}
