package com.checkout.payment.gateway.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  PaymentsRepository paymentsRepository;

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    PostPaymentResponse payment = new PostPaymentResponse();
    payment.setId(UUID.randomUUID());
    payment.setAmount(10);
    payment.setCurrency("USD");
    payment.setStatus(PaymentStatus.AUTHORIZED);
    payment.setExpiryMonth(12);
    payment.setExpiryYear(2024);
    payment.setCardNumberLastFour(4321);

    paymentsRepository.add(payment);

    mvc.perform(MockMvcRequestBuilders.get("/api/v1/payment/" + payment.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(payment.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.getCurrency()))
        .andExpect(jsonPath("$.amount").value(payment.getAmount()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/api/v1/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Page not found"));
  }

  @Test
  void whenPaymentIsRequestedWithValidCreditCardNumber() throws Exception {
    PostPaymentRequest postPaymentRequest = new PostPaymentRequest();
    postPaymentRequest.setCardNumber("2222405343248877");
    postPaymentRequest.setExpiryMonth("4");
    postPaymentRequest.setExpiryYear("2026");
    postPaymentRequest.setCurrency("GBP");
    postPaymentRequest.setAmount(100);
    postPaymentRequest.setCvv("123");

    ObjectMapper objectMapper = new ObjectMapper();

    mvc.perform(MockMvcRequestBuilders.post("/api/v1/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postPaymentRequest)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty());
  }

  @Test
  void whenPaymentIsRequestedWithInvalidCreditCardNumber() throws Exception {
    PostPaymentRequest postPaymentRequest = new PostPaymentRequest();
    postPaymentRequest.setCardNumber("222240534324887A");
    postPaymentRequest.setExpiryMonth("4");
    postPaymentRequest.setExpiryYear("2026");
    postPaymentRequest.setCurrency("GBP");
    postPaymentRequest.setAmount(100);
    postPaymentRequest.setCvv("123");

    ObjectMapper objectMapper = new ObjectMapper();

    mvc.perform(MockMvcRequestBuilders.post("/api/v1/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postPaymentRequest)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("card_number must contain only digits"))
        .andExpect(jsonPath("$.status").value("Rejected"));
  }

  @Test
  void whenPaymentIsRequestedWithInvalidExpiryDate() throws Exception {
    PostPaymentRequest postPaymentRequest = new PostPaymentRequest();
    postPaymentRequest.setCardNumber("2222405343248877");
    postPaymentRequest.setExpiryMonth("4");
    postPaymentRequest.setExpiryYear("2025");
    postPaymentRequest.setCurrency("GBP");
    postPaymentRequest.setAmount(100);
    postPaymentRequest.setCvv("123");

    ObjectMapper objectMapper = new ObjectMapper();

    mvc.perform(MockMvcRequestBuilders.post("/api/v1/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postPaymentRequest)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Expiry date must be in the future"))
        .andExpect(jsonPath("$.status").value("Rejected"));
  }

}
