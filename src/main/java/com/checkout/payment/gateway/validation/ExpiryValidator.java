package com.checkout.payment.gateway.validation;


import com.checkout.payment.gateway.model.CreatePaymentRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.YearMonth;

public class ExpiryValidator implements ConstraintValidator<ValidExpiry, CreatePaymentRequest> {

  @Override
  public boolean isValid(CreatePaymentRequest request, ConstraintValidatorContext constraintValidatorContext) {
    Integer expiryMonth = request.getExpiryMonth();
    Integer expiryYear = request.getExpiryYear();

    boolean expiryMonthIsValid = false;
    boolean expiryYearIsValid = false;

    if (request.getExpiryMonth() == null) {
      constraintValidatorContext.buildConstraintViolationWithTemplate("Expiry month must not be null")
          .addConstraintViolation()
          .disableDefaultConstraintViolation();
    } else {
      expiryMonthIsValid = true;
    }

    if (request.getExpiryYear() == null) {
      constraintValidatorContext.buildConstraintViolationWithTemplate("Expiry year must not be null")
          .addConstraintViolation()
          .disableDefaultConstraintViolation();
    } else {
      expiryYearIsValid = true;
    }

    if (!expiryMonthIsValid || !expiryYearIsValid) {
      return false;
    }

    YearMonth expiryYearMonth = YearMonth.of(expiryYear, expiryMonth);
    constraintValidatorContext.buildConstraintViolationWithTemplate("Expiry date must be in the future")
        .addConstraintViolation()
        .disableDefaultConstraintViolation();
    return expiryYearMonth.isAfter(YearMonth.now());
  }
}
