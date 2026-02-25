package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class PostPaymentRequest implements Serializable {

  @PositiveOrZero
  @Size(min = 14, max = 19, message = "Credit or Debit Card number must be between 14 and 19 digits")
  @JsonProperty("card_number")
  private String cardNumber;

  @Min(value = 1, message = "Month must between 1 and 12")
  @Max(value = 12, message = "Month must between 1 and 12")
  @JsonProperty("expiry_month")
  private int expiryMonth;

  @Future
  @JsonProperty("expiry_year")
  private int expiryYear;

  @Size(min = 3, max = 3)
  private String currency;

  @NotNull
  @PositiveOrZero
  private int amount;

  @Size(min = 3, max = 4, message = "CVV must be 3-4 digits long")
  private int cvv;

  public PostPaymentRequest() {
  }

  public PostPaymentRequest(String cardNumber, int expiryMonth,
      int expiryYear, String currency, int amount, int cvv) {
    this.cardNumber = cardNumber;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
    this.currency = currency;
    this.amount = amount;
    this.cvv = cvv;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public int getExpiryMonth() {
    return expiryMonth;
  }

  public void setExpiryMonth(int expiryMonth) {
    this.expiryMonth = expiryMonth;
  }

  public int getExpiryYear() {
    return expiryYear;
  }

  public void setExpiryYear(int expiryYear) {
    this.expiryYear = expiryYear;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public int getCvv() {
    return cvv;
  }

  public void setCvv(int cvv) {
    this.cvv = cvv;
  }

  @JsonProperty("expiry_date")
  public String getExpiryDate() {
    return String.format("%d/%d", expiryMonth, expiryYear);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
