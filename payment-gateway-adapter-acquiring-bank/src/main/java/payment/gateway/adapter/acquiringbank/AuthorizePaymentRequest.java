package payment.gateway.adapter.acquiringbank;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;
import lombok.Builder;

@Builder
record AuthorizePaymentRequest(@JsonProperty("card_number") String cardNumber,
                               @JsonProperty("expiry_date") String expiryDate, String currency,
                               BigInteger amount, String cvv) {

}
