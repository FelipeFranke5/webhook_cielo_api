package dev.franke.felipe.webhook_cielo_api.api.exception_handler;

import dev.franke.felipe.webhook_cielo_api.api.exception.InvalidNotificationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NotificationExceptionHandler {

  @ExceptionHandler(InvalidNotificationException.class)
  public ResponseEntity<?> handleInvalidNotification(InvalidNotificationException exception) {
    if (exception.getMessage().equals("Invalid payload: PaymentId is required")) {
      ResponseErrorDetail detail =
          new ResponseErrorDetail(
              NotificationErrorCode.PAYMENT_ID_REQUIRED.getCode(), exception.getMessage());
      return ResponseEntity.badRequest().body(detail);
    } else if (exception.getMessage().contains("should be UUID")) {
      ResponseErrorDetail detail =
          new ResponseErrorDetail(
              NotificationErrorCode.INVALID_PAYMENT_OR_RECURRENT_PAYMENT_ID.getCode(),
              exception.getMessage());
      return ResponseEntity.badRequest().body(detail);
    }
    ResponseErrorDetail detail =
        new ResponseErrorDetail(NotificationErrorCode.OTHER.getCode(), exception.getMessage());
    return ResponseEntity.badRequest().body(detail);
  }
}
