package payment.gateway.adapter.acquiringbank;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.restclient.autoconfigure.service.HttpServiceClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.service.registry.ImportHttpServices;
import paymentgateway.domain.port.out.AcquiringBankPort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AutoConfiguration(before = HttpServiceClientAutoConfiguration.class)
@ImportHttpServices(group = "acquiring-bank", types = AcquiringBank.class)
final class AcquiringBankPortAutoConfiguration {

  @Bean
  static AcquiringBankPort acquiringBankPort(final AcquiringBank acquiringBank) {
    return new AcquiringBankAdapter(acquiringBank);
  }
}
