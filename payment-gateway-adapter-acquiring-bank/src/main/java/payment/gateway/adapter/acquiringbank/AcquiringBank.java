package payment.gateway.adapter.acquiringbank;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

interface AcquiringBank {

  @PostExchange("/payments")
  AuthorizePaymentResponse authorizePayment(
      @RequestBody AuthorizePaymentRequest authorizePaymentRequest);
}
