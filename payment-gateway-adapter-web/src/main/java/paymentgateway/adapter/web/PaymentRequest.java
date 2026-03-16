package paymentgateway.adapter.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

record PaymentRequest(@NotNull @Pattern(regexp = "^\\d{14,19}$") String cardNumber,
                      @NotNull @Min(1L) @Max(12L) Integer expiryMonth,
                      @NotNull @PositiveOrZero Integer expiryYear,
                      @NotNull @Pattern(regexp = "^[A-Z]{3}$") String currency,
                      @NotNull @Min(1) Long amount,
                      @NotNull @Pattern(regexp = "^\\d{3,4}$") String cvv) {

}
