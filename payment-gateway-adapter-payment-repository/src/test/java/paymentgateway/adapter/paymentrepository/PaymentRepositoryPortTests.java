package paymentgateway.adapter.paymentrepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.YearMonth;
import java.util.Currency;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import paymentgateway.domain.model.MaskedCard;
import paymentgateway.domain.model.MonetaryAmount;
import paymentgateway.domain.model.Payment;
import paymentgateway.domain.model.PaymentId;
import paymentgateway.domain.model.PaymentStatus;
import paymentgateway.domain.port.out.PaymentRepositoryPort;

@SpringBootTest
@EnableAutoConfiguration
@SpringBootConfiguration(proxyBeanMethods = false)
final class PaymentRepositoryPortTests {

  @Autowired
  private PaymentRepositoryPort subject;

  @Test
  void whenPaymentNotYetSavedThenRepositoryIsEmptyAndWhenSavedThenPaymentIsRetrievable() {
    final var id = PaymentId.builder()
        .value(UUID.randomUUID())
        .build();

    assertThat(subject.findById(id))
        .isEmpty();

    final var payment = Payment.builder()
        .id(id)
        .card(MaskedCard.builder()
            .last4Digits("0123")
            .expiry(YearMonth.now().plusMonths(1L))
            .build())
        .amount(MonetaryAmount.builder()
            .currency(Currency.getInstance("USD"))
            .value(1L)
            .build())
        .status(PaymentStatus.AUTHORIZED)
        .build();

    subject.save(payment);

    assertThat(subject.findById(payment.id()))
        .contains(payment);
  }
}
