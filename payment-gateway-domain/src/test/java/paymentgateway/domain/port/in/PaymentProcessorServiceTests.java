package paymentgateway.domain.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.YearMonth;
import java.util.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import paymentgateway.domain.exception.AcquiringBankException;
import paymentgateway.domain.model.MaskedCard;
import paymentgateway.domain.model.MonetaryAmount;
import paymentgateway.domain.model.Payment;
import paymentgateway.domain.model.PaymentStatus;
import paymentgateway.domain.model.UnmaskedCard;
import paymentgateway.domain.port.out.AcquiringBankPort;
import paymentgateway.domain.port.out.BankAuthorizationResult.Authorized;
import paymentgateway.domain.port.out.BankAuthorizationResult.Declined;
import paymentgateway.domain.port.out.BankAuthorizationResult.Failed;
import paymentgateway.domain.port.out.PaymentRepositoryPort;

@ExtendWith(MockitoExtension.class)
final class PaymentProcessorServiceTests {

  @Mock
  private AcquiringBankPort acquiringBank;
  @Mock
  private PaymentRepositoryPort paymentRepository;
  @InjectMocks
  private PaymentProcessorService subject;

  @Test
  void acquiringBankPortIsRequired() {
    assertThatThrownBy(() -> new PaymentProcessorService(null, paymentRepository))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("acquiringBankPort is marked non-null but is null");
  }

  @Test
  void paymentRepositoryPortIsRequired() {
    assertThatThrownBy(() -> new PaymentProcessorService(acquiringBank, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("paymentRepositoryPort is marked non-null but is null");
  }

  @Test
  void commandIsRequired() {
    assertThatThrownBy(() -> subject.processPayment(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("command is marked non-null but is null");
  }

  @Test
  void acquiringBankAuthorized() {
    final var cardNumber = "01234567890123";
    final var cardExpiry = YearMonth.now().plusMonths(1L);
    final var cardSecurityCode = "123";
    final var amount = 1L;
    final var currency = "GBP";

    when(acquiringBank.authorize(any(UnmaskedCard.class), any(MonetaryAmount.class)))
        .thenReturn(new Authorized("123456"));

    final var payment = subject.processPayment(ProcessPaymentCommand.builder()
        .cardNumber(cardNumber)
        .cardExpiryMonth(cardExpiry.getMonthValue())
        .cardExpiryYear(cardExpiry.getYear())
        .cardSecurityCode(cardSecurityCode)
        .amount(amount)
        .currency(currency)
        .build());

    assertThat(payment)
        .returns(MaskedCard.builder()
            .last4Digits("0123")
            .expiry(cardExpiry)
            .build(), Payment::card)
        .returns(MonetaryAmount.builder()
            .value(amount)
            .currency(Currency.getInstance(currency))
            .build(), Payment::amount)
        .returns(PaymentStatus.AUTHORIZED, Payment::status);

    verify(acquiringBank)
        .authorize(UnmaskedCard.builder()
            .number(cardNumber)
            .expiry(cardExpiry)
            .securityCode(cardSecurityCode)
            .build(), MonetaryAmount.builder()
            .value(amount)
            .currency(Currency.getInstance(currency))
            .build());
    verify(paymentRepository)
        .save(payment);
  }

  @Test
  void acquiringBankDeclined() {
    final var cardNumber = "01234567890123";
    final var cardExpiry = YearMonth.now().plusMonths(1L);
    final var cardSecurityCode = "123";
    final var amount = 1L;
    final var currency = "GBP";

    when(acquiringBank.authorize(any(UnmaskedCard.class), any(MonetaryAmount.class)))
        .thenReturn(new Declined());

    final var payment = subject.processPayment(ProcessPaymentCommand.builder()
        .cardNumber(cardNumber)
        .cardExpiryMonth(cardExpiry.getMonthValue())
        .cardExpiryYear(cardExpiry.getYear())
        .cardSecurityCode(cardSecurityCode)
        .amount(amount)
        .currency(currency)
        .build());

    assertThat(payment)
        .returns(MaskedCard.builder()
            .last4Digits("0123")
            .expiry(cardExpiry)
            .build(), Payment::card)
        .returns(MonetaryAmount.builder()
            .value(amount)
            .currency(Currency.getInstance(currency))
            .build(), Payment::amount)
        .returns(PaymentStatus.DECLINED, Payment::status);

    verify(acquiringBank)
        .authorize(UnmaskedCard.builder()
            .number(cardNumber)
            .expiry(cardExpiry)
            .securityCode(cardSecurityCode)
            .build(), MonetaryAmount.builder()
            .value(amount)
            .currency(Currency.getInstance(currency))
            .build());
    verify(paymentRepository)
        .save(payment);
  }

  @Test
  void acquiringBankFailed() {
    final var cardNumber = "01234567890123";
    final var cardExpiry = YearMonth.now().plusMonths(1L);
    final var cardSecurityCode = "123";
    final var amount = 1L;
    final var currency = "GBP";

    when(acquiringBank.authorize(any(UnmaskedCard.class), any(MonetaryAmount.class)))
        .thenReturn(new Failed());

    assertThatThrownBy(() -> subject.processPayment(ProcessPaymentCommand.builder()
        .cardNumber(cardNumber)
        .cardExpiryMonth(cardExpiry.getMonthValue())
        .cardExpiryYear(cardExpiry.getYear())
        .cardSecurityCode(cardSecurityCode)
        .amount(amount)
        .currency(currency)
        .build()))
        .isInstanceOf(AcquiringBankException.class);

    verify(acquiringBank)
        .authorize(UnmaskedCard.builder()
            .number(cardNumber)
            .expiry(cardExpiry)
            .securityCode(cardSecurityCode)
            .build(), MonetaryAmount.builder()
            .value(amount)
            .currency(Currency.getInstance(currency))
            .build());
    verifyNoInteractions(paymentRepository);
  }
}
