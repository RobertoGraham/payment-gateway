package paymentgateway.domain.port.out;

import java.util.Optional;
import paymentgateway.domain.model.Payment;
import paymentgateway.domain.model.PaymentId;

public interface PaymentRepositoryPort {

  void save(Payment payment);

  Optional<Payment> findById(PaymentId id);
}
