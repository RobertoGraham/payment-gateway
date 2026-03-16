package paymentgateway.domain.port.in;

import lombok.Builder;

@Builder
public record ProcessPaymentCommand(String cardNumber, int cardExpiryMonth, int cardExpiryYear,
                                    String cardSecurityCode, long amount, String currency) {

}
