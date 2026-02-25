package com.checkout.payment.gateway.mapper;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.model.ProcessPaymentResponse;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class PostPaymentResponseMapperTest {

  private final PostPaymentResponseMapper postPaymentResponseMapper = PostPaymentResponseMapper.INSTANCE;

  @Test
  public void shouldMapPostPaymentRequestAndProcessPaymentResponseToPostPaymentResponse() {
    PostPaymentRequest postPaymentRequest = new PostPaymentRequest(
        "2222405343248877", 8, 2027, "GBP", 20000, 123
    );

    UUID id = UUID.randomUUID();
    ProcessPaymentResponse processPaymentResponse = new ProcessPaymentResponse(true, id);
    PostPaymentResponse result = postPaymentResponseMapper.toPostPaymentResponse(postPaymentRequest, processPaymentResponse);

    assertThat(result.getId()).isEqualTo(processPaymentResponse.getAuthorisationCode());
    assertThat(result.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    assertThat(result.getExpiryMonth()).isEqualTo(postPaymentRequest.getExpiryMonth());
    assertThat(result.getExpiryMonth()).isEqualTo(postPaymentRequest.getExpiryMonth());
    assertThat(result.getExpiryYear()).isEqualTo(postPaymentRequest.getExpiryYear());
    assertThat(result.getCardNumberLastFour()).isEqualTo(Integer.parseInt(postPaymentRequest.getCardNumber().substring(12, 16)));
    assertThat(result.getCurrency()).isEqualTo(postPaymentRequest.getCurrency());
    assertThat(result.getAmount()).isEqualTo(postPaymentRequest.getAmount());
  }

  @Test
  public void shouldReturnZeroIfCardNumberIsNull() {
    String cardNumber = null;
    assertThat(postPaymentResponseMapper.toCardNumberLastFour(cardNumber)).isEqualTo(0);
  }

  @Test
  public void shouldReturnPaymentStatusAuthorizedIfApiReturnsTrue() {
    boolean value = true;
    assertThat(postPaymentResponseMapper.map(value)).isEqualTo(PaymentStatus.AUTHORIZED);
  }

  @Test
  public void shouldReturnPaymentStatusDeclinedIfApiReturnsFalse() {
    boolean value = false;
    assertThat(postPaymentResponseMapper.map(value)).isEqualTo(PaymentStatus.DECLINED);
  }

  @Test
  public void shouldReturnNullIfBothPostPaymentRequestAndProcessPaymentResponseAreNull() {
    PostPaymentRequest postPaymentRequest = null;
    ProcessPaymentResponse processPaymentResponse = null;
    assertThat(postPaymentResponseMapper.toPostPaymentResponse(postPaymentRequest, processPaymentResponse)).isNull();
  }

  @Test
  public void shouldReturnNullValuesMappedFromNullPostPaymentRequest() {
    UUID id = UUID.randomUUID();
    ProcessPaymentResponse processPaymentResponse = new ProcessPaymentResponse(true, id);
    PostPaymentRequest postPaymentRequest = null;

    PostPaymentResponse result = postPaymentResponseMapper.toPostPaymentResponse(postPaymentRequest, processPaymentResponse);

    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    assertThat(result.getExpiryMonth()).isEqualTo(0);
    assertThat(result.getExpiryYear()).isEqualTo(0);
    assertThat(result.getCardNumberLastFour()).isEqualTo(0);
    assertThat(result.getCurrency()).isNull();
    assertThat(result.getAmount()).isEqualTo(0);
  }

  @Test
  public void shouldReturnNullValuesMappedFromNullProcessPaymentRequest() {
    PostPaymentRequest postPaymentRequest = new PostPaymentRequest(
        "2222405343248877", 8, 2027, "GBP", 20000, 123
    );

    ProcessPaymentResponse processPaymentResponse = null;
    PostPaymentResponse result = postPaymentResponseMapper.toPostPaymentResponse(postPaymentRequest, processPaymentResponse);

    assertThat(result.getId()).isEqualTo(null);
    assertThat(result.getStatus()).isEqualTo(null);
    AssertionsForClassTypes.assertThat(result.getExpiryMonth()).isEqualTo(8);
    AssertionsForClassTypes.assertThat(result.getExpiryYear()).isEqualTo(2027);
    AssertionsForClassTypes.assertThat(result.getCardNumberLastFour()).isEqualTo(8877);
    AssertionsForClassTypes.assertThat(result.getCurrency()).isEqualTo("GBP");
    AssertionsForClassTypes.assertThat(result.getAmount()).isEqualTo(20000);
  }
}
