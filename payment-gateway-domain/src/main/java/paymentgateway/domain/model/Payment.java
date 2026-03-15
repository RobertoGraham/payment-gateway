package paymentgateway.domain.model;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record Payment(@NonNull PaymentId id, @NonNull MaskedCard card,
                      @NonNull MonetaryAmount amount, @NonNull PaymentStatus status) {

}
