package dev.franke.felipe.webhook_cielo_api.api.exception;

public class NotificationNotFoundException extends RuntimeException {

  public NotificationNotFoundException(String message) {
    super(message);
  }
}
