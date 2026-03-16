package paymentgateway.adapter.acquiringbank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
record AuthorizePaymentRequest(@JsonProperty("card_number") String cardNumber,
                               @JsonProperty("expiry_date") String expiryDate, String currency,
                               long amount, String cvv) {

}
