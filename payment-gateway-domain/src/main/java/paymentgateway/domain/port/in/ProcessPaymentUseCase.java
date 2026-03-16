package paymentgateway.domain.port.in;

import paymentgateway.domain.model.Payment;
import paymentgateway.domain.port.out.AcquiringBankPort;
import paymentgateway.domain.port.out.PaymentRepositoryPort;

public interface ProcessPaymentUseCase {

  static ProcessPaymentUseCase newProcessPaymentUseCase(final AcquiringBankPort acquiringBankPort,
      final PaymentRepositoryPort paymentRepositoryPort) {
    return new PaymentProcessorService(acquiringBankPort, paymentRepositoryPort);
  }

  Payment processPayment(ProcessPaymentCommand command);
}
