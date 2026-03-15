package paymentgateway.adapter.acquiringbank;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.math.BigInteger;
import java.time.YearMonth;
import java.util.Currency;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.circuitbreaker.retry.CircuitBreakerRetryPolicy;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import paymentgateway.domain.model.MonetaryAmount;
import paymentgateway.domain.model.UnmaskedCard;
import paymentgateway.domain.port.out.AcquiringBankPort;
import paymentgateway.domain.port.out.BankAuthorizationResult.Authorized;
import paymentgateway.domain.port.out.BankAuthorizationResult.Declined;
import paymentgateway.domain.port.out.BankAuthorizationResult.Failed;

@Testcontainers
@SpringBootTest
@EnableAutoConfiguration
@SpringBootConfiguration(proxyBeanMethods = false)
final class AcquiringBankPortTests {

  private static final String BANK_SIMULATOR_SERVICE_HOST = "bank_simulator";
  private static final int BANK_SIMULATOR_SERVICE_PORT = 8080;

  @Container
  private static final ComposeContainer ENVIRONMENT = new ComposeContainer(
      new File("../compose.yaml"))
      .withExposedService(BANK_SIMULATOR_SERVICE_HOST, BANK_SIMULATOR_SERVICE_PORT);

  @Autowired
  private AcquiringBankPort subject;

  @DynamicPropertySource
  static void dynamicProperties(final DynamicPropertyRegistry registry) {
    registry.add("spring.http.serviceclient.acquiring-bank.base-url",
        () -> "http://%s:%d".formatted(
            ENVIRONMENT.getServiceHost(BANK_SIMULATOR_SERVICE_HOST, BANK_SIMULATOR_SERVICE_PORT),
            ENVIRONMENT.getServicePort(BANK_SIMULATOR_SERVICE_HOST, BANK_SIMULATOR_SERVICE_PORT)));
  }

  @BeforeEach
  void resetCircuitBreaker(@Autowired final CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
    Optional.of(circuitBreakerFactory.create("AcquiringBankAdapter.authorize"))
        .filter(FrameworkRetryCircuitBreaker.class::isInstance)
        .map(FrameworkRetryCircuitBreaker.class::cast)
        .map(FrameworkRetryCircuitBreaker::getCircuitBreakerPolicy)
        .ifPresent(CircuitBreakerRetryPolicy::reset);
  }

  @Test
  void authorized() {
    assertThat(subject.authorize(UnmaskedCard.builder()
        .number("2222405343248871")
        .expiry(YearMonth.now().plusMonths(1L))
        .securityCode("123")
        .build(), MonetaryAmount.builder()
        .value(BigInteger.ONE)
        .currency(Currency.getInstance("GBP"))
        .build()))
        .isInstanceOf(Authorized.class);
  }

  @Test
  void declined() {
    assertThat(subject.authorize(UnmaskedCard.builder()
        .number("2222405343248872")
        .expiry(YearMonth.now().plusMonths(1L))
        .securityCode("123")
        .build(), MonetaryAmount.builder()
        .value(BigInteger.ONE)
        .currency(Currency.getInstance("GBP"))
        .build()))
        .isInstanceOf(Declined.class);
  }

  @Test
  void failed() {
    assertThat(subject.authorize(UnmaskedCard.builder()
        .number("2222405343248870")
        .expiry(YearMonth.now().plusMonths(1L))
        .securityCode("123")
        .build(), MonetaryAmount.builder()
        .value(BigInteger.ONE)
        .currency(Currency.getInstance("GBP"))
        .build()))
        .isInstanceOf(Failed.class);
  }
}
