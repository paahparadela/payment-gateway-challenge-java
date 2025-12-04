package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.mapper.PaymentMapper;
import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PostAcquirerRequest;
import com.checkout.payment.gateway.model.PostAcquirerResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  @Value("${acquirer.api.url}")
  private String acquirerApiBaseUrl;

  private final PaymentsRepository paymentsRepository;

  private final RestTemplate restTemplate;

  private final PaymentMapper paymentMapper;

  public PaymentGatewayService(PaymentsRepository paymentsRepository, RestTemplate restTemplate, PaymentMapper paymentMapper) {
    this.paymentsRepository = paymentsRepository;
    this.restTemplate = restTemplate;
    this.paymentMapper = paymentMapper;
  }

  public GetPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to payment with ID {}", id);
    Payment payment = paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
    return paymentMapper.paymentToGetPaymentResponse(payment);
  }

  public PostPaymentResponse processPayment(PostPaymentRequest postPaymentRequest) {

    if (YearMonth.of(Integer.parseInt(postPaymentRequest.getExpiryYear()),
        Integer.parseInt(postPaymentRequest.getExpiryMonth())).isBefore(YearMonth.now())){
      throw new DateTimeException("Expiry date must be in the future");
    }

    PostAcquirerRequest postAcquirerRequest = paymentMapper
        .postPaymentRequestToPostAcquirerRequest(postPaymentRequest);

    String paymentResourceUrl = acquirerApiBaseUrl + "/payments";

    HttpEntity<PostAcquirerRequest> request = new HttpEntity<>(postAcquirerRequest);

    PostAcquirerResponse postAcquirerResponse;

    try {
      postAcquirerResponse = restTemplate
          .postForObject(paymentResourceUrl, request, PostAcquirerResponse.class);
    } catch (Exception ex) {
      postAcquirerResponse = new PostAcquirerResponse(false, null);
    }

    PostPaymentResponse postPaymentResponse = paymentMapper
        .postPaymentRequestAndPostAcquirerResponseToPostPaymentResponse(postPaymentRequest, postAcquirerResponse);

    paymentsRepository.add(paymentMapper.postPaymentResponseToPayment(postPaymentResponse));

    return postPaymentResponse;
  }
}
