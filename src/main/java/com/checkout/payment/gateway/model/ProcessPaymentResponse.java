package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class ProcessPaymentResponse {

  private boolean authorized;
  @JsonProperty("authorization_code")
  private UUID authorisationCode;

  public ProcessPaymentResponse() {
  }

  public ProcessPaymentResponse(boolean authorized, UUID authorisationCode) {
    this.authorized = authorized;
    this.authorisationCode = authorisationCode;
  }

  public boolean isAuthorized() {
    return authorized;
  }

  public void setAuthorized(boolean authorized) {
    this.authorized = authorized;
  }

  public UUID getAuthorisationCode() {
    return authorisationCode;
  }

  public void setAuthorisationCode(UUID authorisationCode) {
    this.authorisationCode = authorisationCode;
  }

  @Override
  public String toString() {
    return "ProcessPaymentResponse{" +
        "authorized=" + authorized +
        ", authorisationCode=" + authorisationCode +
        '}';
  }
}


