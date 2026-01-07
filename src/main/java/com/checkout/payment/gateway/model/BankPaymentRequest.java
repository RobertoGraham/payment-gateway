package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BankPaymentRequest {

  @JsonProperty("card_number")
  private String cardNumber;

  @JsonProperty("expiry_date")
  private String expiryDate;

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("amount")
  private Integer amount;

  @JsonProperty("cvv")
  private String cvv;

  public BankPaymentRequest(String cardNumber, String expiryDate, String currency, Integer amount,
      String cvv) {
    this.cardNumber = cardNumber;
    this.expiryDate = expiryDate;
    this.currency = currency;
    this.amount = amount;
    this.cvv = cvv;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public String getExpiryDate() {
    return expiryDate;
  }

  public String getCurrency() {
    return currency;
  }

  public Integer getAmount() {
    return amount;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }
}
