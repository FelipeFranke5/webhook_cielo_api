package dev.franke.felipe.webhook_cielo_api.api.exception;

public class InvalidNotificationIdException extends RuntimeException {

    public InvalidNotificationIdException(String message) {
        super(message);
    }

}
