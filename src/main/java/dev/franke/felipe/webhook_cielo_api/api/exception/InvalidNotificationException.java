package dev.franke.felipe.webhook_cielo_api.api.exception;

public class InvalidNotificationException extends RuntimeException {
    public InvalidNotificationException(String message) {
        super(message);
    }
}
