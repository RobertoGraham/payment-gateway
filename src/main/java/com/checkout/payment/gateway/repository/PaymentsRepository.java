package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.model.CreatePaymentRequest;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentsRepository {

  private final HashMap<UUID, ProcessedPayment> payments = new HashMap<>();

  public void add(ProcessedPayment payment) {
    payments.put(payment.id(), payment);
  }

  public Optional<ProcessedPayment> get(UUID id) {
    return Optional.ofNullable(payments.get(id));
  }

  public Optional<ProcessedPayment> exists(CreatePaymentRequest paymentRequest) {
    return payments.values().stream()
        .filter(processedPayment -> processedPayment.cardNumberLastFour().equals(
            Integer.parseInt(paymentRequest.getCardNumber()
                .substring(paymentRequest.getCardNumber().length() - 4))
        ) &&
            processedPayment.expiryMonth().equals(paymentRequest.getExpiryMonth()) &&
            processedPayment.expiryYear().equals(paymentRequest.getExpiryYear()) &&
            processedPayment.currency().equals(paymentRequest.getCurrency()) &&
            processedPayment.amount().equals(paymentRequest.getAmount()))
        .findFirst();
  }

}
