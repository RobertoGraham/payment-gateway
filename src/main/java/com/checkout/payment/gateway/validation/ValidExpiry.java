package com.checkout.payment.gateway.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidExpiryValidator.class)
@Documented
public @interface ValidExpiry {

  String message() default "expiry_month/expiry_year must be in the future";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
