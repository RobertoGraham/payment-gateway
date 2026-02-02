package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.validation.ValidCurrency;
import com.checkout.payment.gateway.validation.ValidExpiry;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import java.io.Serializable;

@Validated
@ValidExpiry
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatePaymentRequest implements Serializable {

  @NotNull(message = "Card number must not be null")
  @Size(min = 14, max = 19, message = "Card number must be between 14 and 19 digits")
  @Pattern(regexp = "\\d+", message = "Card number must contain only digits")
  private final String cardNumber;
  @Min(value = 1, message = "Expiry month must be between 1 and 12")
  @Max(value = 12, message = "Expiry month must be between 1 and 12")
  @JsonProperty("expiry_month")
  private final Integer expiryMonth;
  @JsonProperty("expiry_year")
  private final Integer expiryYear;
  @NotNull(message = "Currency must not be null")
  @ValidCurrency
  private final String currency;
  @NotNull(message = "Amount must not be null")
  private final int amount;
  @NotNull(message = "CVV must not be null")
  @Size(min = 3, max = 4, message = "CVV must be between 3 and 4 digits")
  @Pattern(regexp = "\\d+", message = "CVV must contain only digits")
  private final String cvv;

  @JsonCreator
  public CreatePaymentRequest(
      String cardNumber,
      Integer expiryMonth,
      Integer expiryYear,
      String currency,
      int amount,
      String cvv
  ) {
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

  public Integer getExpiryMonth() {
    return expiryMonth;
  }

  public Integer getExpiryYear() {
    return expiryYear;
  }

  public String getCurrency() {
    return currency;
  }

  public int getAmount() {
    return amount;
  }

  public String getCvv() {
    return cvv;
  }

  @JsonProperty("expiry_date")
  public String getExpiryDate() {
    return String.format("%d/%d", expiryMonth, expiryYear);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumber=" + cardNumber +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
