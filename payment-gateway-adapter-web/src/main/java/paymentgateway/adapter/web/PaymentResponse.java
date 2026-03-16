package paymentgateway.adapter.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
record PaymentResponse(String id, Status status, String last4Digits, int expiryMonth,
                       int expiryYear, String currency) {

  enum Status {

    @JsonProperty("Authorized")
    AUTHORIZED,

    @JsonProperty("Declined")
    DECLINED
  }
}
