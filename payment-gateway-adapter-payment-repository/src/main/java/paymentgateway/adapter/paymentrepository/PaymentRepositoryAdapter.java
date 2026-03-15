package paymentgateway.adapter.paymentrepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import paymentgateway.domain.model.Payment;
import paymentgateway.domain.model.PaymentId;
import paymentgateway.domain.port.out.PaymentRepositoryPort;

final class PaymentRepositoryAdapter implements PaymentRepositoryPort {

  private final Map<PaymentId, Payment> payments = new ConcurrentHashMap<>();

  @Override
  public void save(final Payment payment) {
    payments.put(payment.id(), payment);
  }

  @Override
  public Optional<Payment> findById(final PaymentId id) {
    return Optional.ofNullable(payments.get(id));
  }
}
