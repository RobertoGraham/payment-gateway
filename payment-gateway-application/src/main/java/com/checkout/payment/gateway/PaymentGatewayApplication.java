package com.checkout.payment.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import paymentgateway.domain.port.in.ProcessPaymentUseCase;
import paymentgateway.domain.port.out.AcquiringBankPort;
import paymentgateway.domain.port.out.PaymentRepositoryPort;

@SpringBootApplication(proxyBeanMethods = false)
final class PaymentGatewayApplication {

  @Bean
  static ProcessPaymentUseCase processPaymentUseCase(final AcquiringBankPort acquiringBankPort,
      final PaymentRepositoryPort paymentRepositoryPort) {
    return ProcessPaymentUseCase.newProcessPaymentUseCase(acquiringBankPort, paymentRepositoryPort);
  }

  static void main() {
    SpringApplication.run(PaymentGatewayApplication.class);
  }
}
