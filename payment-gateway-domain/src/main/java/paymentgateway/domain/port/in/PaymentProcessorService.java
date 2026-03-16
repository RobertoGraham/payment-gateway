package paymentgateway.domain.port.in;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import paymentgateway.domain.model.Payment;
import paymentgateway.domain.port.out.AcquiringBankPort;
import paymentgateway.domain.port.out.PaymentRepositoryPort;

@RequiredArgsConstructor
final class PaymentProcessorService implements ProcessPaymentUseCase {

  @NonNull
  private final AcquiringBankPort acquiringBankPort;
  @NonNull
  private final PaymentRepositoryPort paymentRepositoryPort;

  @Override
  public Payment processPayment(final ProcessPaymentCommand command) {
    return null;
  }
}
