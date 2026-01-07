package com.checkout.payment.gateway.service;

import static com.checkout.payment.gateway.enums.PaymentStatus.AUTHORIZED;
import static com.checkout.payment.gateway.enums.PaymentStatus.DECLINED;

import com.checkout.payment.gateway.client.AcquiringBankClient;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final AcquiringBankClient acquiringBankClient;

  public PaymentGatewayService(PaymentsRepository paymentsRepository,
      AcquiringBankClient acquiringBankClient) {
    this.paymentsRepository = paymentsRepository;
    this.acquiringBankClient = acquiringBankClient;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to payment with ID {}", id);
    return paymentsRepository.get(id)
        .orElseThrow(() -> new EventProcessingException("Payment with ID " + id + " not found"));
  }

  public PostPaymentResponse processPayment(PostPaymentRequest req) {
    PaymentStatus status = callBankAndMapStatus(req);

    UUID id = UUID.randomUUID();
    PostPaymentResponse response = toGatewayResponse(id, status, req);
    paymentsRepository.add(response);

    return response;
  }

  private PaymentStatus callBankAndMapStatus(PostPaymentRequest request) {
    BankPaymentRequest bankRequest = toBankRequest(request);

    try {
      BankPaymentResponse bankResponse = acquiringBankClient.requestAuthorization(bankRequest);
      boolean authorized = bankResponse != null && bankResponse.isAuthorized();
      return authorized ? AUTHORIZED : DECLINED;

    } catch (RestClientResponseException e) {
      LOG.warn("Bank error {}. Mapping to Declined.", e.getStatusCode());
      return DECLINED;

    } catch (RuntimeException e) {
      LOG.warn("Unexpected error calling bank. Mapping to Declined.", e);
      return DECLINED;
    }
  }

  private BankPaymentRequest toBankRequest(PostPaymentRequest request) {
    return new BankPaymentRequest(
        request.getCardNumber(),
        String.format("%02d/%d", request.getExpiryMonth(), request.getExpiryYear()),
        request.getCurrency(),
        request.getAmount(),
        request.getCvv()
    );
  }

  private PostPaymentResponse toGatewayResponse(UUID id, PaymentStatus status,
      PostPaymentRequest request) {
    PostPaymentResponse response = new PostPaymentResponse();
    response.setId(id);
    response.setStatus(status);
    response.setCardNumberLastFour(request.getCardNumberLastFour());
    response.setExpiryMonth(request.getExpiryMonth());
    response.setExpiryYear(request.getExpiryYear());
    response.setCurrency(request.getCurrency());
    response.setAmount(request.getAmount());
    return response;
  }
}
