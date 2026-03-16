package paymentgateway.adapter.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import paymentgateway.domain.exception.DomainValidationException;
import paymentgateway.domain.port.in.ProcessPaymentCommand;
import paymentgateway.domain.port.in.ProcessPaymentUseCase;

@RestController
@RequiredArgsConstructor
final class PaymentController {

  private final ProcessPaymentUseCase processPaymentUseCase;

  @ExceptionHandler(DomainValidationException.class)
  static ProblemDetail handleDomainValidationException() {
    return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, "Rejected");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  static ProblemDetail handleMethodArgumentNotValidException() {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Rejected");
  }

  @PostMapping(path = "/payments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  void processPayment(@Valid @RequestBody final PaymentRequest request) {
    final var payment = processPaymentUseCase.processPayment(ProcessPaymentCommand.builder()
        .currency(request.currency())
        .amount(request.amount())
        .cardExpiryMonth(request.expiryMonth())
        .cardExpiryYear(request.expiryYear())
        .cardNumber(request.cardNumber())
        .cardSecurityCode(request.cvv())
        .build());
  }
}
