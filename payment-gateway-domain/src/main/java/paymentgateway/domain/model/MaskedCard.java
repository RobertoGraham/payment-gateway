package paymentgateway.domain.model;

import java.time.YearMonth;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record MaskedCard(@NonNull String last4Digits, @NonNull YearMonth expiry) {

  public MaskedCard {
    if (!last4Digits.matches("^\\d{4}$")) {
      throw new IllegalArgumentException(
          "last4Digits must only contain numeric characters and be 4 characters");
    }
    if (!expiry.isAfter(YearMonth.now())) {
      throw new IllegalArgumentException("expiry must be in the future");
    }
  }
}
