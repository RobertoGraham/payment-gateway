package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.enums.CurrencyCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class CurrencyValidator implements ConstraintValidator<ValidCurrency, String> {

  @Override
  public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
    if (s.length() != 3) {
      constraintValidatorContext
          .buildConstraintViolationWithTemplate("Currency must be a 3-letter ISO currency code")
          .addConstraintViolation()
          .disableDefaultConstraintViolation();
      return false;
    }

    constraintValidatorContext.buildConstraintViolationWithTemplate("Unrecognised currency code provided")
        .addConstraintViolation()
        .disableDefaultConstraintViolation();

    return Arrays.stream(CurrencyCode.values()).anyMatch(cc -> cc.name().equals(s));
  }
}
