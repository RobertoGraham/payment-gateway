package paymentgateway.domain.port.out;

import paymentgateway.domain.model.MonetaryAmount;
import paymentgateway.domain.model.PaymentCard;

public interface AcquiringBankPort {

  BankAuthorizationResult authorize(PaymentCard paymentCard, MonetaryAmount amount);
}
