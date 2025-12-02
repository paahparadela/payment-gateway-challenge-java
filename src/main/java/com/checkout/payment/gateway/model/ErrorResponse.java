package com.checkout.payment.gateway.model;

public class ErrorResponse {
  private final String message;
  private String status = "Rejected";

  public ErrorResponse(String message) {
    this.message = message;
  }

  public ErrorResponse(String message, String status) {
    this.message = message;
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "ErrorResponse{" +
        "message='" + message + '\'' +
        "status='" + status + '\'' +
        '}';
  }
}
