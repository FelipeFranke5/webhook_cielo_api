package dev.franke.felipe.webhook_cielo_api.api.exception_handler;

import dev.franke.felipe.webhook_cielo_api.api.exception.InvalidNotificationException;
import dev.franke.felipe.webhook_cielo_api.api.exception.InvalidNotificationIdException;
import dev.franke.felipe.webhook_cielo_api.api.exception.NotificationNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NotificationExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationExceptionHandler.class);

    @ExceptionHandler(InvalidNotificationException.class)
    public ResponseEntity<?> handleInvalidNotification(InvalidNotificationException exception) {
        if (exception.getMessage().equals("Invalid payload: PaymentId is required")) {
            ResponseErrorDetail detail = new ResponseErrorDetail(
                    NotificationErrorCode.PAYMENT_ID_REQUIRED.getCode(), exception.getMessage());
            return ResponseEntity.badRequest().body(detail);
        } else if (exception.getMessage().contains("should be UUID")) {
            ResponseErrorDetail detail =
                    new ResponseErrorDetail(NotificationErrorCode.INVALID_FORMAT.getCode(), exception.getMessage());
            return ResponseEntity.badRequest().body(detail);
        }
        ResponseErrorDetail detail =
                new ResponseErrorDetail(NotificationErrorCode.OTHER.getCode(), exception.getMessage());
        return ResponseEntity.badRequest().body(detail);
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotificationNotFoundException exception) {
        ResponseErrorDetail detail =
                new ResponseErrorDetail(NotificationErrorCode.PAYMENT_NOT_FOUND.getCode(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(detail);
    }

    @ExceptionHandler(InvalidNotificationIdException.class)
    public ResponseEntity<?> handleInvalidNotificationId(InvalidNotificationIdException exception) {
        ResponseErrorDetail detail = new ResponseErrorDetail(
                NotificationErrorCode.INVALID_PAYMENT_OR_RECURRENT_PAYMENT_ID.getCode(), exception.getMessage());
        return ResponseEntity.badRequest().body(detail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUknownException(Exception exception) {
        logger.error("\n\n---------------------- EXCEPTION CAUGHT BY THE GLOBAL HANDLER, BUT IT IS AN UNKNOWN TYPE");
        logger.error("\n\n---------------------- EXCEPTION IS: {}", String.valueOf(exception));
        logger.error("\n\n---------------------- EXCEPTION MESSAGE: {}", exception.getMessage());
        ResponseErrorDetail detail =
                new ResponseErrorDetail(NotificationErrorCode.OTHER.getCode(), "Invalid Operation");
        return ResponseEntity.internalServerError().body(detail);
    }
}
