package paymentgateway.adapter.web;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.hv.UUIDValidator;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import paymentgateway.adapter.web.PaymentResponse.Status;
import paymentgateway.domain.exception.AcquiringBankException;
import paymentgateway.domain.exception.DomainValidationException;
import paymentgateway.domain.model.Payment;
import paymentgateway.domain.model.PaymentId;
import paymentgateway.domain.port.in.ProcessPaymentCommand;
import paymentgateway.domain.port.in.ProcessPaymentUseCase;
import paymentgateway.domain.port.in.RetrievePaymentQuery;

@Slf4j
@RestController
final class PaymentController {

  private static final String REJECTED = "Rejected";

  private final ProcessPaymentUseCase processPaymentUseCase;
  private final RetrievePaymentQuery retrievePaymentQuery;
  private final UUIDValidator uuidValidator = new UUIDValidator();

  PaymentController(final ProcessPaymentUseCase processPaymentUseCase,
      final RetrievePaymentQuery retrievePaymentQuery) {
    this.processPaymentUseCase = processPaymentUseCase;
    this.retrievePaymentQuery = retrievePaymentQuery;
    uuidValidator.initialize(
        new AnnotationDescriptor.Builder<>(org.hibernate.validator.constraints.UUID.class,
            Collections.emptyMap())
            .build()
            .getAnnotation());
  }

  @ExceptionHandler(DomainValidationException.class)
  static ProblemDetail handleDomainValidationException() {
    return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, REJECTED);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  static ProblemDetail handleMethodArgumentNotValidException() {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, REJECTED);
  }

  @ExceptionHandler(AcquiringBankException.class)
  static ProblemDetail handleAcquiringBankException() {
    return ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
  }

  private static PaymentResponse toPaymentResponse(final Payment payment) {
    return PaymentResponse.builder()
        .id(payment.id().value().toString())
        .status(switch (payment.status()) {
          case AUTHORIZED -> Status.AUTHORIZED;
          case DECLINED -> Status.DECLINED;
        })
        .last4Digits(payment.card().last4Digits())
        .expiryMonth(payment.card().expiry().getMonthValue())
        .expiryYear(payment.card().expiry().getYear())
        .currency(payment.amount().currency().getCurrencyCode())
        .amount(payment.amount().value())
        .build();
  }

  @PostMapping(path = "/payments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  PaymentResponse processPayment(@Valid @RequestBody final PaymentRequest request) {
    final var payment = processPaymentUseCase.processPayment(ProcessPaymentCommand.builder()
        .currency(request.currency())
        .amount(request.amount())
        .cardExpiryMonth(request.expiryMonth())
        .cardExpiryYear(request.expiryYear())
        .cardNumber(request.cardNumber())
        .cardSecurityCode(request.cvv())
        .build());
    return toPaymentResponse(payment);
  }

  @GetMapping(path = "/payment/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<PaymentResponse> retrievePayment(@PathVariable("id") final String id) {
    if (!uuidValidator.isValid(id, null)) {
      return ResponseEntity.notFound()
          .build();
    }

    return retrievePaymentQuery.retrieve(PaymentId.builder()
            .value(UUID.fromString(id))
            .build())
        .map(PaymentController::toPaymentResponse)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound()
            .build());
  }
}
