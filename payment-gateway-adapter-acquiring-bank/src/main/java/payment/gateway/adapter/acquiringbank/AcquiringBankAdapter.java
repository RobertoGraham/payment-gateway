package payment.gateway.adapter.acquiringbank;

import lombok.RequiredArgsConstructor;
import paymentgateway.domain.model.MonetaryAmount;
import paymentgateway.domain.model.PaymentCard;
import paymentgateway.domain.port.out.AcquiringBankPort;
import paymentgateway.domain.port.out.BankAuthorizationResult;

@RequiredArgsConstructor
final class AcquiringBankAdapter implements AcquiringBankPort {

  private final AcquiringBank acquiringBank;

  @Override
  public BankAuthorizationResult authorize(final PaymentCard paymentCard,
      final MonetaryAmount amount) {
    final var request = AcquiringBankMapper.toAuthorizePaymentRequest(paymentCard, amount);
    final var response = acquiringBank.authorizePayment(request);
    return AcquiringBankMapper.toBankAuthorizationResult(response);
  }
}
