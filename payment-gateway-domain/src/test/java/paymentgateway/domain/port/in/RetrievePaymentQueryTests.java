package paymentgateway.domain.port.in;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import paymentgateway.domain.port.out.PaymentRepositoryPort;

@ExtendWith(MockitoExtension.class)
final class RetrievePaymentQueryTests {

  @Test
  void newRetrievePaymentQuery(@Mock final PaymentRepositoryPort paymentRepositoryPort) {
    assertThat(RetrievePaymentQuery.newRetrievePaymentQuery(paymentRepositoryPort))
        .isInstanceOf(PaymentRetrieverService.class);
  }
}
