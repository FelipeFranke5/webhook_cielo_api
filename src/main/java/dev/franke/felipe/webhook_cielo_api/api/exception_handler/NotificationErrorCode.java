package dev.franke.felipe.webhook_cielo_api.api.exception_handler;

public enum NotificationErrorCode {
    INVALID_PAYMENT_OR_RECURRENT_PAYMENT_ID(1),
    PAYMENT_ID_REQUIRED(2),
    OTHER(3),
    PAYMENT_NOT_FOUND(4),
    INVALID_FORMAT(5);

    private final int code;

    NotificationErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
