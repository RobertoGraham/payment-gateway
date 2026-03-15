package paymentgateway.domain.port.out;

import paymentgateway.domain.model.MonetaryAmount;
import paymentgateway.domain.model.UnmaskedCard;

public interface AcquiringBankPort {

  BankAuthorizationResult authorize(UnmaskedCard unmaskedCard, MonetaryAmount amount);
}
