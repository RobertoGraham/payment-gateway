package paymentgateway.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

final class PaymentIdTests {

  @Test
  void valueIsRequired() {
    assertThatThrownBy(() -> PaymentId.builder()
        .build())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("value is marked non-null but is null");
  }

  @Test
  void valid() {
    assertThat(PaymentId.builder()
        .value(UUID.randomUUID())
        .build())
        .isNotNull();
  }
}
