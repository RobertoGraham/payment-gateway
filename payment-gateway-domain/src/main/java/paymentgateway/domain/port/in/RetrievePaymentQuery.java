package paymentgateway.domain.port.in;

import java.util.Optional;
import paymentgateway.domain.model.Payment;
import paymentgateway.domain.model.PaymentId;
import paymentgateway.domain.port.out.PaymentRepositoryPort;

public interface RetrievePaymentQuery {

  static RetrievePaymentQuery newRetrievePaymentQuery(
      final PaymentRepositoryPort paymentRepositoryPort) {
    return new PaymentRetrieverService(paymentRepositoryPort);
  }

  Optional<Payment> retrieve(PaymentId id);
}
