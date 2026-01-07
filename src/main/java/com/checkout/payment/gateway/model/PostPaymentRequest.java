package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.validation.ValidExpiry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Locale;

@ValidExpiry
public class PostPaymentRequest implements Serializable {

  @JsonProperty("card_number")
  @NotBlank(message = "card_number is required")
  @Pattern(regexp = "^[0-9]{14,19}$", message = "card_number must be 14-19 digits and numeric")
  private String cardNumber;

  @JsonProperty("expiry_month")
  @NotNull(message = "expiry_month is required")
  @Min(value = 1, message = "expiry_month must be between 1 and 12")
  @Max(value = 12, message = "expiry_month must be between 1 and 12")
  private Integer expiryMonth;

  @JsonProperty("expiry_year")
  @NotNull(message = "expiry_year is required")
  private Integer expiryYear;

  @JsonProperty("currency")
  @NotBlank(message = "currency is required")
  @Pattern(regexp = "^(?i)(GBP|USD|EUR)$", message = "currency must be one of [GBP, USD, EUR]")
  private String currency;

  @JsonProperty("amount")
  @NotNull(message = "amount is required")
  @Min(value = 1, message = "amount must be a positive integer")
  private Integer amount;

  @JsonProperty("cvv")
  @NotBlank(message = "cvv is required")
  @Pattern(regexp = "^[0-9]{3,4}$", message = "cvv must be 3-4 digits and numeric")
  private String cvv;

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber == null ? null : cardNumber.trim();
  }

  public Integer getExpiryMonth() {
    return expiryMonth;
  }

  public void setExpiryMonth(Integer expiryMonth) {
    this.expiryMonth = expiryMonth;
  }

  public Integer getExpiryYear() {
    return expiryYear;
  }

  public void setExpiryYear(Integer expiryYear) {
    this.expiryYear = expiryYear;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency == null ? null : currency.trim().toUpperCase(Locale.ROOT);
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv == null ? null : cvv.trim();
  }

  @JsonIgnore
  public String getCardNumberLastFour() {
    if (cardNumber == null) {
      return null;
    }
    String trimmed = cardNumber.trim();
    if (trimmed.length() < 4) {
      return null;
    }
    return trimmed.substring(trimmed.length() - 4);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumberLastFour='" + getCardNumberLastFour() + '\'' +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        '}';
  }
}
