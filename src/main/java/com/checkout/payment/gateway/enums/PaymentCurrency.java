package com.checkout.payment.gateway.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentCurrency {
  GBP("GBP"),
  EUR("EUR"),
  BRL("BRL");

  private final String name;

  PaymentCurrency(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return this.name;
  }
}
