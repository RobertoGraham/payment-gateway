package paymentgateway.domain.model;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record MonetaryAmount(long value, @NonNull Currency currency) {

  public static final Set<Currency> ACCEPTABLE_CURRENCIES = Stream.of("USD", "EUR", "GBP")
      .map(Currency::getInstance)
      .collect(Collectors.toUnmodifiableSet());

  public MonetaryAmount {
    if (value < 1) {
      throw new IllegalArgumentException("value must be positive");
    }
    if (!ACCEPTABLE_CURRENCIES.contains(currency)) {
      throw new IllegalArgumentException("currency must be accepted");
    }
  }
}
