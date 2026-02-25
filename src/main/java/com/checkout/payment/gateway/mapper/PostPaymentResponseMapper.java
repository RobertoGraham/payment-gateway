package com.checkout.payment.gateway.mapper;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.model.ProcessPaymentResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PostPaymentResponseMapper {
  PostPaymentResponseMapper INSTANCE = Mappers.getMapper(PostPaymentResponseMapper.class);

  @Mapping(target = "id", source = "processPaymentResponse.authorisationCode")
  @Mapping(target = "status", source = "processPaymentResponse.authorized")
  @Mapping(target = "cardNumberLastFour", source = "request.cardNumber")
  @Mapping(target = "expiryMonth", source = "request.expiryMonth")
  @Mapping(target = "expiryYear", source = "request.expiryYear")
  @Mapping(target = "currency", source = "request.currency")
  @Mapping(target = "amount", source = "request.amount")
  PostPaymentResponse toPostPaymentResponse(PostPaymentRequest request, ProcessPaymentResponse processPaymentResponse);

  default PaymentStatus map(boolean value) {
    return value ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED;
  }

  default int toCardNumberLastFour(String cardNumber) {
    return cardNumber == null ? 0 : Integer.parseInt(cardNumber.substring(12, 16));
  }
}
