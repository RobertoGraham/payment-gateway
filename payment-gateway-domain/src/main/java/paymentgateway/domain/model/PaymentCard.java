package paymentgateway.domain.model;

import java.time.YearMonth;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record PaymentCard(@NonNull String number, @NonNull YearMonth expiry,
                          @NonNull String securityCode) {

  public PaymentCard {
    if (!number.matches("^\\d{14,19}$")) {
      throw new IllegalArgumentException(
          "number must only contain numeric characters and be between 14-19 characters");
    }
    if (!expiry.isAfter(YearMonth.now())) {
      throw new IllegalArgumentException("expiry must be in the future");
    }
    if (!securityCode.matches("^\\d{3,4}$")) {
      throw new IllegalArgumentException(
          "securityCode must only contain numeric characters and be between 3-4 characters");
    }
  }
}
