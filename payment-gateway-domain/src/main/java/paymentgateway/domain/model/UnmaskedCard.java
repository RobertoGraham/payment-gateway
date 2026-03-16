package paymentgateway.domain.model;

import java.time.YearMonth;
import lombok.Builder;
import lombok.NonNull;
import paymentgateway.domain.exception.DomainValidationException;

@Builder
public record UnmaskedCard(@NonNull String number, @NonNull YearMonth expiry,
                           @NonNull String securityCode) {

  public UnmaskedCard {
    if (!number.matches("^\\d{14,19}$")) {
      throw new DomainValidationException(
          "number must only contain numeric characters and be between 14-19 characters");
    }
    if (!expiry.isAfter(YearMonth.now())) {
      throw new DomainValidationException("expiry must be in the future");
    }
    if (!securityCode.matches("^\\d{3,4}$")) {
      throw new DomainValidationException(
          "securityCode must only contain numeric characters and be between 3-4 characters");
    }
  }

  public MaskedCard mask() {
    return MaskedCard.builder()
        .last4Digits(number.substring(number.length() - 4))
        .expiry(expiry)
        .build();
  }

  @Override
  public String toString() {
    return "PaymentCard{expiry=%s}".formatted(expiry);
  }
}
