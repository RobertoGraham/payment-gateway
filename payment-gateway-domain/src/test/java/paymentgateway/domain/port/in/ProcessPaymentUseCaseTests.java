package paymentgateway.domain.port.in;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import paymentgateway.domain.port.out.AcquiringBankPort;
import paymentgateway.domain.port.out.PaymentRepositoryPort;

@ExtendWith(MockitoExtension.class)
final class ProcessPaymentUseCaseTests {

  @Test
  void newProcessPaymentUseCase(@Mock final AcquiringBankPort acquiringBankPort,
      @Mock final PaymentRepositoryPort paymentRepositoryPort) {
    assertThat(
        ProcessPaymentUseCase.newProcessPaymentUseCase(acquiringBankPort, paymentRepositoryPort))
        .isInstanceOf(PaymentProcessorService.class);
  }
}
