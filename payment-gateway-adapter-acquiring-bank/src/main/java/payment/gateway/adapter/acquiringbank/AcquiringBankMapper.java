package payment.gateway.adapter.acquiringbank;

import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import paymentgateway.domain.model.MonetaryAmount;
import paymentgateway.domain.model.PaymentCard;
import paymentgateway.domain.port.out.BankAuthorizationResult;
import paymentgateway.domain.port.out.BankAuthorizationResult.Authorized;
import paymentgateway.domain.port.out.BankAuthorizationResult.Declined;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class AcquiringBankMapper {

  private static final DateTimeFormatter EXPIRY_DATE = DateTimeFormatter.ofPattern("MM/yyyy");

  static AuthorizePaymentRequest toAuthorizePaymentRequest(final PaymentCard paymentCard,
      final MonetaryAmount monetaryAmount) {
    return AuthorizePaymentRequest.builder()
        .cardNumber(paymentCard.number())
        .expiryDate(paymentCard.expiry().format(EXPIRY_DATE))
        .currency(monetaryAmount.currency().getCurrencyCode())
        .amount(monetaryAmount.value())
        .cvv(paymentCard.securityCode())
        .build();
  }

  static BankAuthorizationResult toBankAuthorizationResult(
      final AuthorizePaymentResponse authorizePaymentResponse) {
    return authorizePaymentResponse.authorized() ?
        new Authorized(authorizePaymentResponse.authorizationCode())
        : new Declined();
  }
}
