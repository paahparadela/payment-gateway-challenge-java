package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.YearMonth;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class PostPaymentRequest implements Serializable {

  @NotNull(message = "card_number is mandatory")
  @Size(min = 14, max = 19, message = "card_number must be between 14 and 19 digits")
  @Pattern(regexp = "^\\d+$", message = "card_number must contain only digits")
  @JsonProperty("card_number")
  private String cardNumber;

  @NotNull(message = "expiry_month is mandatory")
  @Pattern(regexp = "^(1[0-2]|[1-9])$", message = "expiry_month must have value between 1 and 12")
  @JsonProperty("expiry_month")
  private String expiryMonth;

  @NotNull(message = "expiry_year is mandatory")
  @Size(min = 4, max = 4, message = "expiry_year must be 4 digits")
  @Pattern(regexp = "^\\d+$", message = "expiry_year must contain only digits")
  @JsonProperty("expiry_year")
  private String expiryYear;

  @NotBlank(message = "currency is mandatory")
  @Size(min = 3, max = 3, message = "currency must be 3 characters")
  private String currency;

  @NotNull(message = "amount is mandatory")
  private Integer amount;

  @Size(min = 3, max = 4, message = "cvv must be between 3 and 4 digits")
  @Pattern(regexp = "^\\d+$", message = "cvv must contain only digits")
  @NotNull(message = "cvv is mandatory")
  private String cvv;

  public PostPaymentRequest() {}

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getExpiryMonth() {
    return expiryMonth;
  }

  public void setExpiryMonth(String expiryMonth) {
    this.expiryMonth = expiryMonth;
  }

  public String getExpiryYear() {
    return expiryYear;
  }

  public void setExpiryYear(String expiryYear) {
    this.expiryYear = expiryYear;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }

  @JsonProperty("expiry_date")
  public String getExpiryDate() {
    return String.format("%s/%s", expiryMonth, expiryYear);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumber=" + cardNumber +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
