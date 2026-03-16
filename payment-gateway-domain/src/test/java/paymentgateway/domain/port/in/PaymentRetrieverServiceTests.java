package paymentgateway.domain.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.YearMonth;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import paymentgateway.domain.model.MaskedCard;
import paymentgateway.domain.model.MonetaryAmount;
import paymentgateway.domain.model.Payment;
import paymentgateway.domain.model.PaymentId;
import paymentgateway.domain.model.PaymentStatus;
import paymentgateway.domain.port.out.PaymentRepositoryPort;

@ExtendWith(MockitoExtension.class)
final class PaymentRetrieverServiceTests {

  @Mock
  private PaymentRepositoryPort paymentRepository;
  @InjectMocks
  private PaymentRetrieverService subject;

  @Test
  void paymentRepositoryPortIsRequired() {
    assertThatThrownBy(() -> new PaymentRetrieverService(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("paymentRepositoryPort is marked non-null but is null");
  }

  @Test
  void idIsRequired() {
    assertThatThrownBy(() -> subject.retrieve(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("id is marked non-null but is null");
  }

  @Test
  void present() {
    final var id = PaymentId.builder()
        .value(UUID.randomUUID())
        .build();
    final var payment = Payment.builder()
        .id(id)
        .card(MaskedCard.builder()
            .last4Digits("1234")
            .expiry(YearMonth.of(2026, 1))
            .build())
        .status(PaymentStatus.AUTHORIZED)
        .amount(MonetaryAmount.builder()
            .value(1L)
            .currency(Currency.getInstance("GBP"))
            .build())
        .build();

    when(paymentRepository.findById(any(PaymentId.class)))
        .thenReturn(Optional.of(payment));

    assertThat(subject.retrieve(id))
        .contains(payment);

    verify(paymentRepository)
        .findById(id);
  }

  @Test
  void empty() {
    final var id = PaymentId.builder()
        .value(UUID.randomUUID())
        .build();

    when(paymentRepository.findById(any(PaymentId.class)))
        .thenReturn(Optional.empty());

    assertThat(subject.retrieve(id))
        .isEmpty();

    verify(paymentRepository)
        .findById(id);
  }
}
