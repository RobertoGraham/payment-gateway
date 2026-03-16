package paymentgateway.domain.port.in;

import paymentgateway.domain.model.Payment;

public interface ProcessPaymentUseCase {

  Payment processPayment(ProcessPaymentCommand command);
}
