package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.client.AcquiringBankClient;
import com.checkout.payment.gateway.client.AcquiringBankPaymentRequest;
import com.checkout.payment.gateway.client.AcquiringBankPaymentResponse;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.AcquiringBankClientException;
import com.checkout.payment.gateway.exception.PaymentNotFoundException;
import com.checkout.payment.gateway.model.CreatePaymentRequest;
import com.checkout.payment.gateway.model.PaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import com.checkout.payment.gateway.repository.ProcessedPayment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final AcquiringBankClient acquiringBankClient;

  public PaymentGatewayService(
      PaymentsRepository paymentsRepository,
      AcquiringBankClient acquiringBankClient
  ) {
    this.paymentsRepository = paymentsRepository;
    this.acquiringBankClient = acquiringBankClient;
  }

  public PaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.get(id).map(this::mapPostResponseFromRepository).orElseThrow(() -> new PaymentNotFoundException(String.format("Payment with id %s not found", id)));
  }

  public PaymentResponse processPayment(CreatePaymentRequest paymentRequest) {
    LOG.debug("Processing payment request for card number last four: {}", paymentRequest.getCardNumber().substring(paymentRequest.getCardNumber().length() - 4));
    return paymentsRepository.exists(paymentRequest).map(this::mapPostResponseFromRepository).orElseGet(() -> processPaymentRequest(paymentRequest));
  }

  private PaymentResponse processPaymentRequest(CreatePaymentRequest paymentRequest) {
    try {
      AcquiringBankPaymentResponse acquiringBankResponse = acquiringBankClient.makePaymentRequest(new AcquiringBankPaymentRequest(
          paymentRequest.getCardNumber(),
          paymentRequest.getExpiryDate(),
          paymentRequest.getCurrency(),
          paymentRequest.getAmount(),
          paymentRequest.getCvv()
      ));

      paymentsRepository.add(mapToRepository(paymentRequest, acquiringBankResponse));
      return mapFromAcquiringBank(paymentRequest, acquiringBankResponse);
    } catch (AcquiringBankClientException e) {
      LOG.error("Error processing payment request: {}. Returning REJECTED status", e.getMessage());
      return new PaymentResponse(
          UUID.randomUUID(),
          PaymentStatus.REJECTED,
          null,
          null,
          null,
          null,
          null
      );
    }
  }

  private ProcessedPayment mapToRepository(CreatePaymentRequest paymentRequest, AcquiringBankPaymentResponse bankResponse) {
    return new ProcessedPayment(
        bankResponse.authorizationCode(),
        bankResponse.authorized() ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED,
        Integer.parseInt(paymentRequest.getCardNumber().substring(paymentRequest.getCardNumber().length() - 4)),
        paymentRequest.getExpiryMonth(),
        paymentRequest.getExpiryYear(),
        paymentRequest.getCurrency(),
        paymentRequest.getAmount()
    );
  }

  private PaymentResponse mapPostResponseFromRepository(ProcessedPayment payment) {
    return new PaymentResponse(
        payment.id(),
        payment.paymentStatus(),
        payment.cardNumberLastFour(),
        payment.expiryMonth(),
        payment.expiryYear(),
        payment.currency(),
        payment.amount()
    );
  }

  private PaymentResponse mapFromAcquiringBank(CreatePaymentRequest paymentRequest, AcquiringBankPaymentResponse bankResponse) {
    return new PaymentResponse(
        bankResponse.authorizationCode(),
        bankResponse.authorized() ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED,
        Integer.parseInt(paymentRequest.getCardNumber().substring(paymentRequest.getCardNumber().length() - 4)),
        paymentRequest.getExpiryMonth(),
        paymentRequest.getExpiryYear(),
        paymentRequest.getCurrency(),
        paymentRequest.getAmount()
    );
  }
}
