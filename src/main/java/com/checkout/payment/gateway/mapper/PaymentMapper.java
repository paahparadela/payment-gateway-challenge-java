package com.checkout.payment.gateway.mapper;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostAcquirerRequest;
import com.checkout.payment.gateway.model.PostAcquirerResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
  PostAcquirerRequest postPaymentRequestToPostAcquirerRequest(PostPaymentRequest postPaymentRequest);

  @Mapping(target = "id", expression = "java(UUID.randomUUID())")
  @Mapping(target = "status", source = "postAcquirerResponse.authorized")
  @Mapping(target = "cardNumberLastFour", expression = "java(postPaymentRequest.cardNumber.substring(postPaymentRequest.cardNumber.length() - 4))")
  PostPaymentResponse postPaymentRequestAndPostAcquirerResponseToPostPaymentResponse(PostPaymentRequest postPaymentRequest, PostAcquirerResponse postAcquirerResponse);

  default PaymentStatus map(boolean authorized) {
    if (authorized) {
      return PaymentStatus.AUTHORIZED;
    } else {
      return PaymentStatus.DECLINED;
    }
  }
}
