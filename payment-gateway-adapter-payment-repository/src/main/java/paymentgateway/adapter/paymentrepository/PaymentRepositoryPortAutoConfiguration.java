package paymentgateway.adapter.paymentrepository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import paymentgateway.domain.port.out.PaymentRepositoryPort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AutoConfiguration
final class PaymentRepositoryPortAutoConfiguration {

  @Bean
  static PaymentRepositoryPort paymentRepositoryPort() {
    return new PaymentRepositoryAdapter();
  }
}
