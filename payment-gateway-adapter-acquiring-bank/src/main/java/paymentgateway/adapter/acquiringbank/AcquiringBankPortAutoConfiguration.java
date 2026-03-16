package paymentgateway.adapter.acquiringbank;

import java.time.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.restclient.autoconfigure.service.HttpServiceClientAutoConfiguration;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.web.service.registry.ImportHttpServices;

@Import(AcquiringBankAdapter.class)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AutoConfiguration(before = HttpServiceClientAutoConfiguration.class)
@ImportHttpServices(group = "acquiring-bank", types = AcquiringBank.class)
final class AcquiringBankPortAutoConfiguration {

  @Bean
  static Customizer<FrameworkRetryCircuitBreakerFactory> defaultCircuitBreakerFactoryCustomizer() {
    return factory -> factory.configureDefault(id -> new FrameworkRetryConfigBuilder(id)
        .retryPolicy(RetryPolicy.withMaxRetries(3))
        .openTimeout(Duration.ofSeconds(20))
        .resetTimeout(Duration.ofSeconds(5))
        .build());
  }
}
