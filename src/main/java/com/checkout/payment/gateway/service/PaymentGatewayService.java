package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.mapper.PaymentMapper;
import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.PostAcquirerRequest;
import com.checkout.payment.gateway.model.PostAcquirerResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
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

  @Autowired
  private PaymentMapper paymentMapper;

  private final PaymentsRepository paymentsRepository;

  private final RestTemplate restTemplate;

  public PaymentGatewayService(PaymentsRepository paymentsRepository, RestTemplate restTemplate) {
    this.paymentsRepository = paymentsRepository;
    this.restTemplate = restTemplate;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public PostPaymentResponse processPayment(PostPaymentRequest postPaymentRequest) {
    //todo
    PostAcquirerRequest postAcquirerRequest = paymentMapper
        .postPaymentRequestToPostAcquirerRequest(postPaymentRequest);

    String paymentResourceUrl = acquirerApiBaseUrl + "/payments";

    HttpEntity<PostAcquirerRequest> request = new HttpEntity<>(postAcquirerRequest);
    PostAcquirerResponse postAcquirerResponse = restTemplate
        .postForObject(paymentResourceUrl, request, PostAcquirerResponse.class);

    PostPaymentResponse postPaymentResponse = paymentMapper
        .postPaymentRequestAndPostAcquirerResponseToPostPaymentResponse(postPaymentRequest, postAcquirerResponse);

    postPaymentResponse.setId(UUID.randomUUID());

    paymentsRepository.add(postPaymentResponse);

    return postPaymentResponse;
  }
}
