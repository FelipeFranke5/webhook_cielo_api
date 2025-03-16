package dev.franke.felipe.webhook_cielo_api.api.service;

import dev.franke.felipe.webhook_cielo_api.api.exception.InvalidNotificationIdException;
import dev.franke.felipe.webhook_cielo_api.api.exception.NotificationNotFoundException;
import dev.franke.felipe.webhook_cielo_api.api.model.Notification;
import dev.franke.felipe.webhook_cielo_api.api.repository.NotificationRepository;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        notificationRepository = Mockito.mock(NotificationRepository.class);
        notificationService = new NotificationService(notificationRepository);
    }

    @Test
    @DisplayName("Method 'getNotificationByPaymentId' - Valid PaymentId - Should return a Notification")
    public void getNotificationByPaymentId_ValidPaymentId_ShouldReturnNotification() {
        UUID id = UUID.randomUUID();
        String paymentIdUserEntered = id.toString();
        Notification notification = new Notification(paymentIdUserEntered, null, 1);
        Mockito.when(notificationRepository.findByPaymentId(paymentIdUserEntered)).thenReturn(notification);
        Notification result = notificationService.getNotificationByPaymentId(paymentIdUserEntered);
        Assertions.assertNotNull(result, () -> "The notification should not be null");
        Assertions.assertEquals(result, notification, () -> "The notification should be the same as the mock");
    }

    @Test
    @DisplayName("Method 'getNotificationByPaymentId' - Valid PaymentId/Not Found - Should throw NotificationNotFoundException")
    public void getNotificationByPaymentId_ValidPaymentIdNotFound_ShouldThrowNotificationNotFoundException() {
        UUID id = UUID.randomUUID();
        String paymentIdUserEntered = id.toString();
        Mockito.when(notificationRepository.findByPaymentId(paymentIdUserEntered)).thenReturn(null);
        Assertions.assertThrows(
            NotificationNotFoundException.class,
            () -> notificationService.getNotificationByPaymentId(paymentIdUserEntered),
            () -> "The method should throw NotificationNotFoundException when the notification is not found"
        );
    }

    @Test
    @DisplayName("Method 'getNotificationByPaymentId' - Invalid PaymentId - Should throw InvalidNotificationIdException")
    public void getNotificationByPaymentId_InvalidPaymentId_ShouldThrowInvalidNotificationIdException() {
        String paymentIdUserEntered = "invalid-id";
        Assertions.assertThrows(
            InvalidNotificationIdException.class,
            () -> notificationService.getNotificationByPaymentId(paymentIdUserEntered),
            () -> "The method should throw InvalidNotificationIdException when the paymentId is invalid"
        );
    }

}
