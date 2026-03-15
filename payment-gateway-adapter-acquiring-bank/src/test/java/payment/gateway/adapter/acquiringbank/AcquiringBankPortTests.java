package payment.gateway.adapter.acquiringbank;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.math.BigInteger;
import java.time.YearMonth;
import java.util.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import paymentgateway.domain.model.MonetaryAmount;
import paymentgateway.domain.model.PaymentCard;
import paymentgateway.domain.port.out.AcquiringBankPort;
import paymentgateway.domain.port.out.BankAuthorizationResult.Authorized;
import paymentgateway.domain.port.out.BankAuthorizationResult.Declined;

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

  @DynamicPropertySource
  static void dynamicProperties(final DynamicPropertyRegistry registry) {
    registry.add("spring.http.serviceclient.acquiring-bank.base-url",
        () -> "http://%s:%d".formatted(
            ENVIRONMENT.getServiceHost(BANK_SIMULATOR_SERVICE_HOST, BANK_SIMULATOR_SERVICE_PORT),
            ENVIRONMENT.getServicePort(BANK_SIMULATOR_SERVICE_HOST, BANK_SIMULATOR_SERVICE_PORT)));
  }

  @Test
  void authorized(@Autowired AcquiringBankPort acquiringBankPort) {
    assertThat(acquiringBankPort.authorize(PaymentCard.builder()
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
  void declined(@Autowired AcquiringBankPort acquiringBankPort) {
    assertThat(acquiringBankPort.authorize(PaymentCard.builder()
        .number("2222405343248872")
        .expiry(YearMonth.now().plusMonths(1L))
        .securityCode("123")
        .build(), MonetaryAmount.builder()
        .value(BigInteger.ONE)
        .currency(Currency.getInstance("GBP"))
        .build()))
        .isInstanceOf(Declined.class);
  }
}
