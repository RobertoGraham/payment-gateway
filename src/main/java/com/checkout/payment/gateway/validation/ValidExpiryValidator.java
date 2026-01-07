package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.YearMonth;

public class ValidExpiryValidator implements ConstraintValidator<ValidExpiry, PostPaymentRequest> {

  @Override
  public boolean isValid(PostPaymentRequest req, ConstraintValidatorContext ctx) {
    if (req == null) {
      return true;
    }

    Integer month = req.getExpiryMonth();
    Integer year = req.getExpiryYear();

    if (month == null || year == null) {
      return true;
    }
    if (month < 1 || month > 12) {
      return true;
    }

    YearMonth expiry = YearMonth.of(year, month);
    YearMonth now = YearMonth.now();

    if (expiry.isBefore(now)) {
      ctx.disableDefaultConstraintViolation();
      ctx.buildConstraintViolationWithTemplate("expiry_month/expiry_year must be in the future")
          .addPropertyNode("expiryYear")
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}
