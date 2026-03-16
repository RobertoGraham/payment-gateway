package paymentgateway.domain.port.in;

import java.time.YearMonth;
import java.util.Currency;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import paymentgateway.domain.exception.AcquiringBankException;
import paymentgateway.domain.model.MonetaryAmount;
import paymentgateway.domain.model.Payment;
import paymentgateway.domain.model.PaymentId;
import paymentgateway.domain.model.PaymentStatus;
import paymentgateway.domain.model.UnmaskedCard;
import paymentgateway.domain.port.out.AcquiringBankPort;
import paymentgateway.domain.port.out.BankAuthorizationResult.Authorized;
import paymentgateway.domain.port.out.BankAuthorizationResult.Declined;
import paymentgateway.domain.port.out.BankAuthorizationResult.Failed;
import paymentgateway.domain.port.out.PaymentRepositoryPort;

@RequiredArgsConstructor
final class PaymentProcessorService implements ProcessPaymentUseCase {

  @NonNull
  private final AcquiringBankPort acquiringBankPort;
  @NonNull
  private final PaymentRepositoryPort paymentRepositoryPort;

  @Override
  public Payment processPayment(@NonNull final ProcessPaymentCommand command) {
    final var card = UnmaskedCard.builder()
        .number(command.cardNumber())
        .expiry(YearMonth.of(command.cardExpiryYear(), command.cardExpiryMonth()))
        .securityCode(command.cardSecurityCode())
        .build();
    final var amount = MonetaryAmount.builder()
        .value(command.amount())
        .currency(Currency.getInstance(command.currency()))
        .build();

    final var bankAuthorizationResult = acquiringBankPort.authorize(card, amount);

    final var status = switch (bankAuthorizationResult) {
      case Authorized _ -> PaymentStatus.AUTHORIZED;
      case Declined _ -> PaymentStatus.DECLINED;
      case Failed _ -> throw new AcquiringBankException();
    };

    final var payment = Payment.builder()
        .id(PaymentId.builder()
            .value(UUID.randomUUID())
            .build())
        .card(card.mask())
        .status(status)
        .amount(amount)
        .build();

    paymentRepositoryPort.save(payment);

    return payment;
  }
}
