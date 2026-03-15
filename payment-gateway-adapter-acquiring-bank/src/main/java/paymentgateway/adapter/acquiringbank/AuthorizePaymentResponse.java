package paymentgateway.adapter.acquiringbank;

import com.fasterxml.jackson.annotation.JsonProperty;

record AuthorizePaymentResponse(boolean authorized,
                                @JsonProperty("authorization_code") String authorizationCode) {

}
