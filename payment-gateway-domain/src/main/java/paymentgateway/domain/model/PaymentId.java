package paymentgateway.domain.model;

import java.util.UUID;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record PaymentId(@NonNull UUID value) {

}
