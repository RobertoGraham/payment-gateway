package paymentgateway.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import paymentgateway.domain.exception.DomainValidationException;
import paymentgateway.domain.port.in.ProcessPaymentCommand;
import paymentgateway.domain.port.in.ProcessPaymentUseCase;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration
final class PaymentControllerTests {

  @Autowired
  private MockMvcTester mockMvcTester;
  @Autowired
  private ObjectMapper objectMapper;
  @MockitoBean
  private ProcessPaymentUseCase processPaymentUseCase;

  @CsvSource(textBlock = """
      null,                 1,    2026, GBP,  1,    123
      01234567890123,       null, 2026, GBP,  1,    123
      01234567890123,       1,    null, GBP,  1,    123
      01234567890123,       1,    2026, null, 1,    123
      01234567890123,       1,    2026, GBP,  null, 123
      01234567890123,       1,    2026, GBP,  1,    null
      01234567890123456789, 1,    2026, GBP,  1,    123
      abcdefghijklmn,       1,    2026, GBP,  1,    123
      0123456789123,        1,    2026, GBP,  1,    123
      01234567890123,       0,    2026, GBP,  1,    123
      01234567890123,       13,   2026, GBP,  1,    123
      01234567890123,       1,    -1,   GBP,  1,    123
      01234567890123,       1,    2026, gbp,  1,    123
      01234567890123,       1,    2026, GB,   1,    123
      01234567890123,       1,    2026, GBPP, 1,    123
      01234567890123,       1,    2026, GBP,  0,    123
      01234567890123,       1,    2026, GBP,  1,    12
      01234567890123,       1,    2026, GBP,  1,    12345
      01234567890123,       1,    2026, GBP,  1,    abc""", nullValues = "null")
  @ParameterizedTest
  void methodArgumentNotValidException(final String cardNumber, final Integer expiryMonth,
      final Integer expiryYear, final String currency, final Long amount, final String cvv) {
    mockMvcTester.post()
        .uri("/payments")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(
            new PaymentRequest(cardNumber, expiryMonth, expiryYear, currency, amount, cvv)))
        .assertThat()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .bodyJson()
        .isEqualTo("""
            {
              "detail": "Rejected",
              "instance": "/payments",
              "status": 400,
              "title": "Bad Request"
            }""");

    verifyNoInteractions(processPaymentUseCase);
  }

  @Test
  void domainValidationException() {
    when(processPaymentUseCase.processPayment(any(ProcessPaymentCommand.class)))
        .thenThrow(DomainValidationException.class);

    mockMvcTester.post()
        .uri("/payments")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(
            new PaymentRequest("01234567891234", 1, 2026, "GBP", 1L, "123")))
        .assertThat()
        .hasStatus(HttpStatus.UNPROCESSABLE_CONTENT)
        .bodyJson()
        .isEqualTo("""
            {
              "detail": "Rejected",
              "instance": "/payments",
              "status": 422,
              "title": "Unprocessable Content"
            }""");
  }

  @SpringBootConfiguration(proxyBeanMethods = false)
  static final class ContextConfiguration {

  }
}
