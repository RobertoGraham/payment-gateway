package payment.gateway.adapter.acquiringbank;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import paymentgateway.domain.model.MonetaryAmount;
import paymentgateway.domain.model.PaymentCard;
import paymentgateway.domain.port.out.AcquiringBankPort;
import paymentgateway.domain.port.out.BankAuthorizationResult;
import paymentgateway.domain.port.out.BankAuthorizationResult.Failed;

@RequiredArgsConstructor
final class AcquiringBankAdapter implements AcquiringBankPort {

  private final AcquiringBank acquiringBank;
  private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

  @Override
  public BankAuthorizationResult authorize(final PaymentCard paymentCard,
      final MonetaryAmount amount) {
    final var circuitBreaker = circuitBreakerFactory.create("AcquiringBankAdapter.authorize");
    return circuitBreaker.run(() -> {
      final var request = AcquiringBankMapper.toAuthorizePaymentRequest(paymentCard, amount);
      final var response = acquiringBank.authorizePayment(request);
      return AcquiringBankMapper.toBankAuthorizationResult(response);
    }, _ -> new Failed());
  }
}
