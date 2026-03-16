package paymentgateway.domain.port.in;

import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import paymentgateway.domain.model.Payment;
import paymentgateway.domain.model.PaymentId;
import paymentgateway.domain.port.out.PaymentRepositoryPort;

@RequiredArgsConstructor
final class PaymentRetrieverService implements RetrievePaymentQuery {

  @NonNull
  private final PaymentRepositoryPort paymentRepositoryPort;

  @Override
  public Optional<Payment> retrieve(@NonNull final PaymentId id) {
    return paymentRepositoryPort.findById(id);
  }
}
