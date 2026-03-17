package paymentgateway.adapter.acquiringbank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import paymentgateway.domain.model.MonetaryAmount;
import paymentgateway.domain.model.UnmaskedCard;
import paymentgateway.domain.port.out.AcquiringBankPort;
import paymentgateway.domain.port.out.BankAuthorizationResult;
import paymentgateway.domain.port.out.BankAuthorizationResult.Failed;

@Slf4j
@Component
@RequiredArgsConstructor
final class AcquiringBankAdapter implements AcquiringBankPort {

  private final AcquiringBank acquiringBank;
  private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

  @Override
  public BankAuthorizationResult authorize(final UnmaskedCard unmaskedCard,
      final MonetaryAmount amount) {
    final var circuitBreaker = circuitBreakerFactory.create("AcquiringBankAdapter.authorize");
    return circuitBreaker.run(() -> {
      final var request = AcquiringBankMapper.toAuthorizePaymentRequest(unmaskedCard, amount);
      final var response = acquiringBank.authorizePayment(request);
      return AcquiringBankMapper.toBankAuthorizationResult(response);
    }, throwable -> {
      log.error("Error occurred while authorizing payment", throwable);
      return new Failed();
    });
  }
}
