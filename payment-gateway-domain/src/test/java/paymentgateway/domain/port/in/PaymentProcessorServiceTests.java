package paymentgateway.domain.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import paymentgateway.domain.port.out.AcquiringBankPort;
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
  void processPayment() {
    assertThat(subject.processPayment(null))
        .isNull();
  }
}
