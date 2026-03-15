package paymentgateway.adapter.acquiringbank;

import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import paymentgateway.domain.model.MonetaryAmount;
import paymentgateway.domain.model.UnmaskedCard;
import paymentgateway.domain.port.out.BankAuthorizationResult;
import paymentgateway.domain.port.out.BankAuthorizationResult.Authorized;
import paymentgateway.domain.port.out.BankAuthorizationResult.Declined;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class AcquiringBankMapper {

  private static final DateTimeFormatter EXPIRY_DATE = DateTimeFormatter.ofPattern("MM/yyyy");

  static AuthorizePaymentRequest toAuthorizePaymentRequest(final UnmaskedCard unmaskedCard,
      final MonetaryAmount monetaryAmount) {
    return AuthorizePaymentRequest.builder()
        .cardNumber(unmaskedCard.number())
        .expiryDate(unmaskedCard.expiry().format(EXPIRY_DATE))
        .currency(monetaryAmount.currency().getCurrencyCode())
        .amount(monetaryAmount.value())
        .cvv(unmaskedCard.securityCode())
        .build();
  }

  static BankAuthorizationResult toBankAuthorizationResult(
      final AuthorizePaymentResponse authorizePaymentResponse) {
    return authorizePaymentResponse.authorized() ?
        new Authorized(authorizePaymentResponse.authorizationCode())
        : new Declined();
  }
}
